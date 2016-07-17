package weekplanning.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import service.DAL
import weekplanning.controllers.Secured
import weekplanning.model.User
import weekplanning.models.Level

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PrintController extends Controller with Secured {

  val printSecret = "dfsghdghjgfj3456rtgsfdzghe54"

  def checkPrint(projectId: Int, json: String, secret: String): Boolean = {
    val weekIds: Seq[Int] = Json.parse(json).as[Seq[Int]]
    Await.result(DAL.getProjectOwner(projectId), Duration.Inf) match {
      case None => false
      case Some(x) => {
        val hash = getHash(projectId, json, x)
        secret == hash
      }
    }
  }

  def getHash(projectId: Int, json: String, x:User): String = {
   val hash = (json.hashCode.toString + json.hashCode.toString + projectId.hashCode.toString +
        x.username.hashCode.toString + printSecret.hashCode).hashCode.toString
    hash
  }

  def doPrint(projectId: Int, json:String) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read) { check =>
      if(!check) Ok("Du kan ikke se dette projekt") else {
        Await.result(DAL.getProjectOwner(projectId), Duration.Inf) match {
          case None => Ok("project ikke fundet")
          case Some(x) => {
            val hash = getHash(projectId, json, x)
            Redirect(routes.PrintController.print(projectId, json, hash))
          }
        }
      }
    }
  }

  def print(projectId: Int, json:String, secret: String) = Action {
    if(checkPrint(projectId, json, secret)) {
      val weekIds: Seq[Int] = Json.parse(json).as[Seq[Int]]
      val weeks = Await.result(DAL.getWeeks(projectId), Duration.Inf)
          .filter(w => weekIds.contains(w.id))

      val coworkers = Await.result(DAL.getCoworkers(projectId), Duration.Inf)
      val days = Await.result(DAL.getAllDays(projectId), Duration.Inf)

      Ok(views.html.print("print", weeks, coworkers, days))
    }
    else {
      Ok("du har ikke retighed til at se dette projekt.")
    }
  }

}
