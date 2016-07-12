package service

import models._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

trait ScheduleService {
  val db:JdbcProfile#Backend#Database
  val weeks = TableQuery[WeekTableDef]
  val days = TableQuery[DayTableDef]

  def createWeekSchema(): Unit = {
    db.run(weeks.schema.create)
  }

  def createDaySchema(): Unit = {
    db.run(days.schema.create)
  }

  def getWeeks(projectId: Int): Future[Seq[Week]] = {
    db.run(weeks.filter(w => w.projectId === projectId).result)
  }

  def addWeek(week: Week): Future[Try[String]] = {
    val id = Await.result(db.run((weeks returning weeks
      .map(_.id)) += week)
      .map(x => Success(x)).recover{
      case ex:Exception => Failure(ex)
    }, Duration.Inf).getOrElse(0)

    if(id != 0) {
      db.run(days ++= (0 to 6).map(x => Day(0, id, WeekDay(x)))).map(_ => Success("ok"))
        .recover{
          case ex:Exception => Failure(ex)
        }
    } else Future{ Failure(new Exception("Ugen blev ikke oprettet.")) }

  }

  def deleteWeek(weekId: Int): Future[Try[String]] = {
    val con = Await.result(db.run(days.filter(d => d.weekId === weekId).delete)
      .map(i => i == 7), Duration.Inf)
    if(con)
    db.run(weeks.filter(w => w.id === weekId).delete)
      .map(i => if(i < 1) Failure(new Exception("Denne uge findes ikke.")) else Success("ok"))
    else Future{ Failure(new Exception("kunne ikke slette alle dage")) }
  }
}
