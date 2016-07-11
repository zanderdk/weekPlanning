package service

import models.{Week, WeekTableDef}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

trait ScheduleService {
  val db:JdbcProfile#Backend#Database
  val weeks = TableQuery[WeekTableDef]

  def getWeeks(projectId: Int): Future[Seq[Week]] = {
    db.run(weeks.filter(w => w.projectId === projectId).result)
  }

  def addWeek(week: Week): Future[Try[String]] = {
    db.run(weeks += week).map(_ => Success("ok")).recover{
      case ex:Exception => Failure(ex)
    }
  }

  def deleteWeek(weekId: Int): Future[Try[String]] = {
    db.run(weeks.filter(w => w.id === weekId).delete)
      .map(i => if(i < 1) Failure(new Exception("Denne uge findes ikke.")) else Success("ok"))
  }
}
