package weekplanning.controllers

import models.Coworker
import play.api.libs.json.Json
import play.api.mvc.Controller
import service.DAL
import weekplanning.controllers.Secured

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import models.Coworker.coworkerFormats
import weekplanning.models.Level

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class CoworkerController extends Controller with Secured {

  def getCoworkers(id: Int) = withAuth { username => implicit request =>
    val k = for {
     x <- DAL.getUserLevel(id, username)
     z <- DAL.getCoworkers(id)
    } yield (x, z)

    val res = Await.result(k, Duration.Inf)

    val seq: Seq[Coworker] = res._1 match {
      case Some(_) => {
        res._2
      }
      case None => Seq()
    }

    Ok(Json.toJson(seq.sortBy(x => x.name)))
  }

  def getCoworker(projectId: Int, name: String) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read) {check =>
       if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
         Await.result(DAL.getCoworker(projectId, name), Duration.Inf) match {
           case Some(x) => Ok(Json.toJson(x))
           case None => Ok("ingen coworker med det navn")
         }
       }
    }
  }

  def updateCoworker(id: Int, json: String) = withAuth { username => implicit request =>
    val coworker = Json.parse(json).as[Coworker]
    Await.result(DAL.getUserLevel(id, username), Duration.Inf).map(l => {
      if(l.id >= 1) {
        Await.result(DAL.updateCoworker(id, coworker),Duration.Inf) match {
          case Success(x) => Ok("ok")
          case Failure(ex) => Ok(ex.getMessage)
        }
      } else {
        Ok("Du har ikke retigheder til at ændre denne plan.")
      }
    }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
  }

  def addCoworkers(id: Int, json: String) = withAuth { username => implicit request =>
    val coworker = Json.parse(json).as[Coworker]
    Await.result(DAL.getUserLevel(id, username), Duration.Inf).map(l => {
      if(l.id >= 1) {
        Await.result(DAL.getCoworkers(id), Duration.Inf).find(c => c.name == coworker.name)
          .map(_ => Ok("allerede en bruger med det navn")).getOrElse(
          Await.result(DAL.addCoworker(Coworker(0, id, coworker.time, coworker.name)), Duration.Inf) match {
            case Success(_) => Ok("ok")
            case Failure(e) => Ok(e.getMessage)
          }
        )
      } else {
        Ok("Du har ikke retigheder til at ændre denne plan.")
      }
    }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
  }

   def deleteCoworker(id: Int, name: String) = withAuth { username => implicit request =>
    Await.result(DAL.getUserLevel(id, username), Duration.Inf).map(l => {
      if(l.id >= 1) {
        Await.result(DAL.deleteCoworker(id, name), Duration.Inf) match {
          case Success(_) => Ok("ok")
          case Failure(ex) => Ok(ex.getMessage)
        }
      } else {
        Ok("Du har ikke retigheder til at ændre denne plan.")
      }
    }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
  }


}
