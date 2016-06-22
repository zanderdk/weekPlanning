package weekplanning.controllers

import models.Level
import play.api.libs.json.Json
import models.Project.projectFormats
import service.JsonConverters.tuple3Writes
import play.api.mvc.{Action, Controller}
import service.DAL

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import weekplanning._

import scala.util.{Failure, Success}

class Application extends Controller with Secured {

  def test = withAuth { username => implicit request =>
//    DAL.createUserSchema()
//    DAL.createProjectSchema()
//    DAL.createCollaboratesSchema()
//    DAL.createProject("testP", username)
    Await.result(DAL.addCollaboration(1, "majken", Level.Write), Duration.Inf) match {
      case Success(x) => Ok("yes")
      case Failure(x) => Ok(x.getMessage)
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

  def index = withAuth { username => implicit request =>
    Redirect(routes.Application.projects())
  }

  def projects = withAuth { username => implicit request =>
    Ok(views.html.projects(Global.name, "Projects", Global.menu))
  }

}
