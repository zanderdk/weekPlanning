package weekplanning.controllers

import models.{Coworker, WorkType}
import play.api.libs.json.Json
import play.api.mvc.Controller
import service.DAL
import weekplanning.controllers.Secured
import models.WorkType.workTypeFormats

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import models.Coworker.coworkerFormats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class WorkTypeController extends Controller with Secured {

  def getWorkTypes(id: Int) = withAuth { username => implicit request =>
    val k = for {
       x <- DAL.getUserLevel(id, username)
       z <- DAL.getWorkTypes(id)
    } yield (x, z)

    val res = Await.result(k, Duration.Inf)

    val seq: Seq[WorkType] = res._1 match {
      case Some(_) => {
        res._2
      }
      case None => Seq()
    }

    Ok(Json.toJson(seq.sortBy(x => x.name)))
  }

  def updateWorkType(json: String) = withAuth { username => implicit request =>
    val work: WorkType = Json.parse(json).as[WorkType]
    val workTypeId = work.id
    val id = Await.result(DAL.getWorkTypeProjectId(workTypeId), Duration.Inf)
    id match {
      case None => Ok("denne vagt type findes ikke.")
      case Some(i) => {
            Await.result(DAL.getUserLevel(i, username), Duration.Inf).map(l => {
            if(l.id >= 1) {
              val x = !Await.result(DAL.getWorkTypes(i), Duration.Inf)
                .exists(w => w.id != workTypeId && w.name == work.name)
              if(x) {
                Await.result(DAL.updateWorkType(work), Duration.Inf) match {
                  case Success(_) => Ok("ok")
                  case Failure(ex) => Ok(ex.getMessage)
                }
              } else { Ok("Der er allerede en vagt type med dette navn.") }
            } else {
              Ok("Du har ikke retigheder til at ændre denne plan.")
            }
          }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
      }
    }
  }

  def getWorkType(id: Int) = withAuth { username => implicit request =>

    val res = Await.result(DAL.getWorkType(id), Duration.Inf)

    res match {
      case None => Ok("err")
      case Some(x) => {
        Await.result(DAL.getUserLevel(x.projectId, username), Duration.Inf)
          .map(_ => Ok(Json.toJson(x)))
      }.getOrElse(Ok("err"))
    }
  }

  def deleteWorkType(id: Int) = withAuth { username => implicit request =>
    val workTypeId = id
    val projectId = Await.result(DAL.getWorkTypeProjectId(workTypeId), Duration.Inf)
    projectId match {
      case None => Ok("denne vagt type findes ikke.")
      case Some(i) => {
            Await.result(DAL.getUserLevel(i, username), Duration.Inf).map(l => {
            if(l.id >= 1) {
              Await.result(DAL.deleteWorkType(workTypeId), Duration.Inf) match {
                case Success(_) => Ok("ok")
                case Failure(ex) => Ok(ex.getMessage)
              }
            } else {
              Ok("Du har ikke retigheder til at ændre denne plan.")
            }
          }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
      }
    }
  }

  def addWorkType(json: String) = withAuth { username => implicit request =>
    val work: WorkType = Json.parse(json).as[WorkType]
    val id: Int = work.projectId
    Await.result(DAL.getUserLevel(id, username), Duration.Inf).map(l => {
      if(l.id >= 1) {
        Await.result(DAL.getWorkTypes(id), Duration.Inf).find(w => w.name == work.name)
          .map(_ => Ok("Der findes allerede en vagt type med det valgte navn.")).getOrElse(
          Await.result(DAL.addWorkType(work), Duration.Inf) match {
            case Success(_) => Ok("ok")
            case Failure(e) => Ok(e.getMessage)
          }
        )
      } else {
        Ok("Du har ikke retigheder til at ændre denne plan.")
      }
    }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
  }



}
