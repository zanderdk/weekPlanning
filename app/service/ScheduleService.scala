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

  def getWeekFromDay(day: Day): Future[Option[Week]] = {
    db.run(weeks.filter(w => w.id === day.weekId).result.headOption)
  }
  
  def findWeek(searchFunction: (WeekTableDef => Rep[Boolean])): Future[Option[Week]] = {
    db.run(weeks.filter(searchFunction).result.headOption)
  }

  def getDays(weekId: Int): Future[Seq[Day]] = {
    db.run(days.filter(d => d.weekId === weekId).result)
  }

  def updateWeek(week:Week): Future[Try[String]] = {
    val id = week.id
    val q = for { w <- weeks if w.id === id } yield (w.year, w.weekNo)
    val k = q.update((week.year, week.weekNo)).map(i => if(i > 0) Success("ok") else Failure{ new Exception("denne vagt type findes ikke.")})
    db.run(k)
  }

  def addWeek(week: Week): Future[Try[String]] = {
    val id = Await.result(db.run((weeks returning weeks
      .map(_.id)) += week)
      .map(x => Success(x)).recover{
      case ex:Exception => Failure(ex)
    }, Duration.Inf).getOrElse(0)

    if(id != 0) {
      db.run(days ++= (1 to 7).map(x => Day(0, id, WeekDay(x)))).map(_ => Success("ok"))
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
