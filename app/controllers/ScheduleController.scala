package weekplanning.controllers

import akka.stream.FlowMonitorState.Failed
import models._
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}
import service.DAL
import weekplanning.models.Level
import weekplanning.models.Level.Level
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class ScheduleController extends Controller with Secured {

  def getDays(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read, None, Some(weekId)){check =>

      val days:Seq[Day] = if(check) {
        val q = for {
          q1 <- DAL.getDays(weekId)
          q2 <- DAL.getDutys(weekId)
        } yield (q1, q2)

        val res = Await.result(q, Duration.Inf)
        res._1.map{ d =>
          val day = Day.unapply(d).get
          new Day(day._1, day._2, day._3) {
            override lazy val dutys = res._2.filter(dd => dd.dayId == day._1)
          }
        }

      } else Seq()

      Ok(Json.toJson(days))
    }
  }

  def getNextWeek(projectId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read) {check =>
      if (!check) Ok("") else {
        val weeks = Await.result(DAL.getWeeks(projectId), Duration.Inf)
        val week = weeks.sortBy(w => (w.year, w.weekNo)).reverse.head
        val nextWeekYear = if(week.weekNo == 53) week.year + 1 else week.year
        val nextWeekNo = if(week.weekNo == 53) 1 else week.weekNo + 1
        Ok(Json.toJson(Week(0, projectId, nextWeekYear, nextWeekNo)))
      }
    }
  }

  def getDutys(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read, None, Some(weekId)) {check =>
      val dutys: Seq[Duty] = if(!check) Seq() else {
        Await.result(DAL.getDutys(weekId), Duration.Inf)
      }
      Ok(Json.toJson(dutys))
    }
  }

  def getDuty(projectId: Int, dutyId: Int)  = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read, None, None, None, Some(dutyId)) {check =>
      if(!check) {
        Ok("du har ikke retigheder til at se dette projekt")
      } else {
        Await.result(DAL.getDuty(dutyId), Duration.Inf) match {
          case Some(x) => Ok(Json.toJson(x))
          case None => Ok("denne vagt blev ikke fundet")
        }
      }
    }
  }


  def addDutys(projectId: Int, json: String) = withAuth { username => implicit request =>
    val dutys = Json.parse(json).as[Seq[Duty]]
    DAL.checkUser(projectId, username, Level.Write, None, None, None, None) { check =>
      if(!check) Ok("du har ikke retighed til at tilføje vagtet") else {
        val week = Await.result(DAL.getDay(dutys(0).dayId), Duration.Inf).get.week
        val ids = week.days.map(x => x.id)
        val idds = dutys.map(x => x.dayId)
        val ch = idds.foldLeft(true) {
          case (x, y) => {
            x && ids.contains(y)
          }
        }
        if(ch) {
          Await.result(DAL.addDutys(dutys), Duration.Inf) match {
            case Success(_) => Ok("ok")
            case Failure(ex) => Ok(ex.getMessage)
          }
        } else {
          Ok("Du kan ikke ændre dette projekt.")
        }
      }
    }
  }

  def getWeek(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Read, None, Some(weekId)){check =>
      if(!check) Ok("err")
      else {
        Await.result(DAL.findWeek(w => w.id === weekId), Duration.Inf) match {
          case Some(x) => Ok(Json.toJson(x))
          case None => Ok("err")
        }
      }
    }
  }

  def deleteDuty(projectId: Int, dutyId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Write, None, None, None, Some(dutyId)) { check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
        Await.result(DAL.deleteDuty(dutyId), Duration.Inf) match {
          case Success(_) => Ok("ok")
          case Failure(ex) => Ok(ex.getMessage)
        }
      }
    }
  }

  def deleteWeek(projectId: Int, weekId: Int) = withAuth { username => implicit request =>
    DAL.checkUser(projectId, username, Level.Write, None, Some(weekId)) { check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt") else {
        Await.result(DAL.deleteWeek(weekId), Duration.Inf) match {
          case Success(_) => Ok("ok")
          case Failure(ex) => Ok(ex.getMessage)
        }
      }
    }
  }

  def updateDuty(projectId: Int, json: String) = withAuth { username => implicit resquest =>
    val duty = Json.parse(json).as[Duty]

    DAL.checkUser(projectId, username, Level.Write, None, None, None, Some(duty.id)){ check =>
      if(!check) Ok("du har ikke retighed til at ændre dette projekt.")
      else {
        Await.result(DAL.updateDuty(duty), Duration.Inf) match {
          case Failure(ex) => Ok(ex.getMessage)
          case Success(_) => Ok("ok")
        }
      }
    }
  }



  def updateWeek(json: String) = withAuth { username => implicit resquest =>
    val week = Json.parse(json).as[Week]

    DAL.checkUser(week.projectId, username, Level.Write, None, Some(week.id)){ check =>
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

      val weeks = lst.view.sortBy(w => (w.year, w.weekNo)).reverse.force

      Ok(Json.toJson(weeks))
    }
  }

}
