package weekplanning.controllers

import models.Location
import play.api.libs.json.Json
import play.api.mvc.Controller
import service.DAL
import weekplanning.controllers.Secured
import models.Location.locationFormats
import weekplanning.models.Level

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class LocationController extends Controller with Secured {

  def addLocation(json: String) = withAuth { username => implicit request =>
    val location = Json.parse(json).as[Location]
    DAL.checkUser(location.projectId, username, Level.Write) {check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
        val loc = Await.result(DAL.getLocations(location.projectId), Duration.Inf)
          .find(l => l.name == location.name)
        loc match {
          case None => {
            Await.result(DAL.addLocation(location), Duration.Inf) match {
              case Failure(ex) => Ok(ex.getMessage)
              case Success(_) => Ok("ok")
            }
          }
          case Some(x) => Ok("Der findes allerede en lokation med dette navn.")
        }
      }
    }
  }
    //todo: problem
   def deleteLocation(projectId: Int, locationId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Write, Some(locationId)) {check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
        Await.result(DAL.deleteLocation(locationId), Duration.Inf) match {
          case Failure(ex) => Ok(ex.getMessage)
          case Success(_) => Ok("ok")
        }
      }
    }
  }

  def updateLocation(json: String) = withAuth { username => implicit request =>
    val location = Json.parse(json).as[Location]
    DAL.checkUser(location.projectId, username, Level.Write, Some(location.id)) {check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
         val loc = Await.result(DAL.getLocations(location.projectId), Duration.Inf)
          .find(l => l.name == location.name && l.id != location.id && l.projectId == location.projectId)
        loc match {
          case None => {
            Await.result(DAL.updateLocation(location), Duration.Inf) match {
              case Failure(ex) => Ok(ex.getMessage)
              case Success(_) => Ok("ok")
            }
          }
          case Some(x) => Ok("Der findes allerede en lokation med dette navn.")
        }
      }
    }
  }

  def getLocations(projectId:Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read) {check =>
    val locations: Seq[Location] = if(!check) Seq() else {
        Await.result(DAL.getLocations(projectId), Duration.Inf)
      }
      Ok(Json.toJson(locations.sortBy(x => x.name)))
    }
  }

  def getLocation(projectId:Int, locationId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read, Some(locationId)) {check => //overvej at flytte check her til
    val location: Option[Location] = if(!check) None else {
        Await.result(DAL.getLocation(locationId), Duration.Inf)
      }
      location match {
        case None => Ok("denne location blev ikke fundet.")
        case Some(x) => Ok(Json.toJson(x))
      }
    }
  }

}
