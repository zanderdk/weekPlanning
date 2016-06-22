package weekplanning.controllers

import models.Level
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import service.DAL
import models.Project.projectFormats
import service.JsonConverters.tuple3Writes

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class ProjectController extends Controller with Secured {

  val addProjcetForm = Form(
    single(
      "projectName" -> text
    )
  )

  def addProject = withAuth { username => implicit request =>
    addProjcetForm.bindFromRequest.fold(
      formWithErrors => Ok("error"),
      projectName => {
        val x = Await.result(DAL.createProject(projectName, username), Duration.Inf)
        x match {
          case Failure(ex) => Ok(ex.getMessage)
          case Success(_) => Ok("ok")
        }
      }
    )
  }

  def getProject(projectId: Int) = withAuth { username => implicit request =>
    val project = Await.result(DAL.usersProjects(username), Duration.Inf)
      .filter{ case(p, l) => (l == Level.Owner || l == Level.Write) && p.id == projectId
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
