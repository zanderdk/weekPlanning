package weekplanning.controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import service.DAL
import service.JsonConverters._
import weekplanning.model.User
import weekplanning.model.User.userFormat
import weekplanning.models.{Level, Project}
import weekplanning.models.Level.{Level, stringToLevel}
import service.JsonConverters.tuple2Reads
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import weekplanning.models.{Level, Project}

class ProjectController extends Controller with Secured {

  val singleForm = Form(
    single(
      "data" -> text
    )
  )

  val updateProjcetForm = Form(
    tuple(
      "id" -> number,
      "name" -> text
    )
  )

  def canEdit(id: Int, username:String) = Await.result(DAL.usersProjects(username), Duration.Inf)
      .find(_._1.id == id)
      .exists(_ => true)

  def addProject = withAuth { username => implicit request =>
    singleForm.bindFromRequest.fold(
      formWithErrors => Ok("error"),
      projectName => {
        val x = Await.result(DAL.createProject(projectName, username), Duration.Inf)
        x match {
          case Failure(ex) => Ok(ex.getMessage)
          case Success(x) => {
            Ok(x.toString)
          }
        }
      }
    )
  }

  def updateProject() = withAuth { username => implicit request =>
    updateProjcetForm.bindFromRequest.fold(
      formWithErrors => Ok("error"),
      pro => {
        val (id, name) = pro
        val projectWithSameName = Await.result(DAL.usersProjects(username), Duration.Inf).find {
          case (p, Level.Owner) => p.name == name
          case _ => false
        }.map(_ => "Du har allerede et projekt med dette navn.")
        val canEdit:Boolean = Await.result(DAL.usersProjects(username), Duration.Inf)
          .find {
            case (p, Level.Owner) => p.id == id
            case _ => false
          }.exists(_ => true)
        if (canEdit)
          if (projectWithSameName.isEmpty) {
            Await.result(DAL.updateProject(Project(id, name)), Duration.Inf) match {
              case Failure(ex) => Ok(ex.getMessage)
              case Success(_) => Ok("ok")
            }
          } else { Ok(projectWithSameName.get) }
        else { Ok("du kan ikke ændre dette projekt") }
      }
    )
  }

  def updateCollaborators() = withAuth { username => implicit request =>
     singleForm.bindFromRequest.fold(
      formWithErrors => Ok("error"),
       jsonString => {
         val json = Json.parse(jsonString)
         val id = (json \ "id").as[Int]
         val users:Seq[(User, Level)] = (json \ "lst").as[Seq[(User, String)]]
           .map(x => {
             val level = stringToLevel(x._2)
             (x._1, level)
           })

         val multipleOfSameUser = users.map(_._1.id).toSet.size != users.map(_._1.id).size
         if(multipleOfSameUser) { Ok("Du kan ikke samarbejde med den samme bruger flere gange") }
         else {
         Await.result(DAL.usersProjects(username), Duration.Inf)
              .find( x => x._1.id == id)
         match {
           case Some((pro, Level.Owner)) => {
             val usr = Await.result(DAL.getUser(username), Duration.Inf)

             usr match {
               case Some(x) => {
                 val lst:Seq[(User, Level)] = users :+ (x, Level.Owner)
                 Await.result(DAL.updateProjectCollaborators(id, lst), Duration.Inf)
                   match {
                   case Success(_) => { Ok("ok") }
                   case Failure(z) => Ok(z.getMessage)
                 }
               }
               case _ => Ok("error")
             }
           }
           case None => Ok("du har ikke retighed til at ændre dette projekt")
         }
         }
      }
    )
  }

  def getCollaborators(id: Int) = withAuth { username => implicit request =>
    if(canEdit(id, username)) {
      val users = Await.result(DAL.getCollaborators(id), Duration.Inf)
        .filter(_._1.username != username).map(x => {
        (x._1.copy(password = ""), x._2)
      })
      Ok(Json.toJson(users))
    } else {
      Ok("Du har ikke retigheder til at ændre dette projekt.")
    }
  }

  def deleteProject(id: Int) = withAuth { username => implicit request =>
   val canEdit:Boolean = Await.result(DAL.usersProjects(username), Duration.Inf)
          .find {
            case (p, Level.Owner) => p.id == id
            case _ => false
          }.exists(_ => true)

      if(canEdit) {
        val k = for {
          y <- DAL.deleteProject(id)
        } yield (y)
        val res = Await.result(k, Duration.Inf)
        res match {
          case (Success(_)) => Ok("ok")
          case _ => Ok("error")
        }
      } else {
        Ok("Du har ikke retigheder til at ændre dette projket.")
      }
  } //todo fix så den slætter alt

  def getProject(id: Int) = withAuth { username => implicit request =>
    val project = Await.result(DAL.usersProjects(username), Duration.Inf)
      .filter{ case(p, l) => (l == Level.Owner || l == Level.Write) && p.id == id
      }.map(_._1).headOption

    project match {
      case Some(p) => Ok(Json.toJson(p))
      case _ => Ok("Projektet blev desvære ikke fundet.")
    }
  }

  def getProjectList = withAuth { username => implicit request =>
    val pro = Await.result(DAL.userProjectsAndOwner(username), Duration.Inf)
      .map{ case (p, l, o) =>
        val owner = if (l == Level.Owner) "Mig" else o
        (p, l, owner)
      }
    val json = Json.toJson(pro)
    Ok(json)
  }

}
