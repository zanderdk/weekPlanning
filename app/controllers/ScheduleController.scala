package weekplanning.controllers

import akka.stream.FlowMonitorState.Failed
import models.{Day, Week, WeekTableDef}
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}
import service.DAL
import weekplanning.models.Level
import weekplanning.models.Level.Level
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class ScheduleController extends Controller with Secured {

  //todo fix check user til at tjekke for weekId

  def getDays(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read){check =>

      val days:Seq[Day] = if(check) {
        Await.result(DAL.getDays(weekId), Duration.Inf)
      } else Seq()

      Ok(Json.toJson(days))
    }
  }

  def getWeek(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read){check =>
      if(!check) Ok("err")
      else {
        Await.result(DAL.findWeek(w => w.id === weekId), Duration.Inf) match {
          case Some(x) => Ok(Json.toJson(x))
          case None => Ok("err")
        }
      }
    }
  }

  def deleteWeek(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Write) { check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
        Await.result(DAL.deleteWeek(weekId), Duration.Inf) match {
          case Success(_) => Ok("ok")
          case Failure(ex) => Ok(ex.getMessage)
        }
      }
    }
  }

  def updateWeek(json: String) = withAuth { username => implicit resquest =>
    val week = Json.parse(json).as[Week]

    DAL.checkUser(week.projectId, username, Level.Write){ check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt.")
      else {
        Await.result(DAL.findWeek{w =>
          w.id =!= week.id && w.projectId === week.projectId && w.year === week.year && w.weekNo === week.weekNo
        }
          , Duration.Inf).map(_ => Ok("Denne uge findes allerede."))
          .getOrElse{
            Await.result(DAL.updateWeek(week), Duration.Inf) match {
              case Success(_) => Ok("ok")
              case Failure(ex) => Ok(ex.getMessage)
            }
          }
      }
    }
  }

  def addWeek(projectId: Int, year: Int, weekNo: Int) = withAuth { username => implicit resquest =>
    DAL.checkUser(projectId, username, Level.Write){ check =>
      if(check) {
        Await.result(DAL.getWeeks(projectId), Duration.Inf)
          .find(w => w.year == year && w.weekNo == weekNo)
          .map(_ => Ok("Denne uge findes allerede"))
          .getOrElse {
            Await.result(DAL.addWeek(Week(0, projectId, year, weekNo)), Duration.Inf) match {
              case Success(_) => Ok("ok")
              case Failure(ex) => Ok(ex.getMessage)
            }
          }
      } else { Ok("du har ikke retighed til at ændre dette projekt.") }
    }
  }

  def getWeeks(projectId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read){check =>
      val lst:Seq[Week] =
        if(check) {
          Await.result(DAL.getWeeks(projectId), Duration.Inf)
        } else {
          Seq()
        }

      Ok(Json.toJson(lst))
    }
  }

}
