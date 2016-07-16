package weekplanning.controllers

import models.Coworker
import play.api.libs.json.Json
import play.api.mvc.Controller
import service.DAL
import weekplanning.controllers.Secured

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import models.Coworker.coworkerFormats
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class CoworkerController extends Controller with Secured {

  def getCoworkers(id: Int) = withAuth { username => implicit request =>
    val k = for {
     x <- DAL.getUserLevel(id, username)
     z <- DAL.getCoworker(id)
    } yield (x, z)

    val res = Await.result(k, Duration.Inf)

    val seq: Seq[Coworker] = res._1 match {
      case Some(_) => {
        res._2
      }
      case None => Seq()
    }

    Ok(Json.toJson(seq))
  }



  def updateCoworker(id: Int, oldName:String, name: String) = withAuth { username => implicit request =>
    Await.result(DAL.getUserLevel(id, username), Duration.Inf).map(l => {
      if(l.id >= 1) {
        Await.result(DAL.getCoworker(id), Duration.Inf).find(c => c.name == name) match {
          case Some(_) => Ok("allerede en bruger med dette navn.")
          case None => {
            Await.result(DAL.updateCoworker(id, oldName, name), Duration.Inf) match {
              case Success(x) => Ok(x)
              case Failure(e) => Ok(e.getMessage)
            }
          }
        }
      } else {
        Ok("Du har ikke retigheder til at ændre denne plan.")
      }
    }).getOrElse( Ok("Du har ikke retigheder til at ændre denne plan.") )
  }

  def addCoworkers(id: Int, name: String) = withAuth { username => implicit request =>
    Await.result(DAL.getUserLevel(id, username), Duration.Inf).map(l => {
      if(l.id >= 1) {
        Await.result(DAL.getCoworker(id), Duration.Inf).find(c => c.name == name)
          .map(_ => Ok("allerede en bruger med det navn")).getOrElse(
          Await.result(DAL.addCoworker(Coworker(0, id, 37.0, name)), Duration.Inf) match {
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
