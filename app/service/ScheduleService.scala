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
  val dutys = TableQuery[DutyTableDef]
  val coworkers: TableQuery[CoworkerTableDef]
  val workTypes: TableQuery[WorkTypeTableDef]
  val locations: TableQuery[LocationTableDef]

  def createWeekSchema(): Unit = {
    db.run(weeks.schema.create)
  }

  def createDutySchema(): Unit = {
    db.run(dutys.schema.create)
  }

  def createDaySchema(): Unit = {
    db.run(days.schema.create)
  }

  def getDayFromDuty(dayId: Int): Future[Option[Day]] = {
    db.run(days.filter(d => d.id === dayId).result.headOption)
  }

  def getCoworkerFromDuty(coworkerId: Int): Future[Option[Coworker]] = {
    db.run(coworkers.filter(c => c.id === coworkerId).result.headOption)
  }

  def getWorkTypeFromDuty(workTypeId: Int) : Future[Option[WorkType]] = {
    db.run(workTypes.filter(w => w.id === workTypeId).result.headOption)
  }

  def addDutys(duty: Seq[Duty]): Future[Try[String]] = {
    db.run(dutys ++= duty).map(_ => Success("ok"))
      .recover{
        case ex:Exception => Failure(ex)
      }
  }

  def getDuty(dutyId: Int): Future[Option[Duty]] = {
    val q = for {
      d <- dutys if d.id === dutyId
      c <- coworkers if c.id === d.coworkerId
      wo <- workTypes if wo.id === d.workTypeId
    } yield (d, c, wo)
    val k = db.run(q.result).map(x => {
      x.map(y => {
        val dutyTup = Duty.unapply(y._1).get
        new Duty(dutyTup._1, dutyTup._2, dutyTup._3, dutyTup._4, dutyTup._5) {
          override lazy val coworker: Coworker = y._2
          override lazy val workType: WorkType = y._3
        }
      }).headOption
    })
    k
  }

  def getCoworkerDutys(coworkerId: Int): Future[Seq[Duty]] = {
    db.run(dutys.filter(d => d.coworkerId === coworkerId).result)
  }

  def getAllDays(projectId: Int): Future[Seq[Day]] = {
    val q = for {
      w <- weeks if w.projectId === projectId
      d <- days if w.id === d.weekId
    } yield d
    db.run(q.result)
  }

  def getAllDutysForCoworker(projectId: Int, coworkerId: Int): Future[Seq[(Int, Double)]] = {
    val q = for {
      d <- days
      du <- dutys if du.dayId === d.id && du.coworkerId === coworkerId
      w <- workTypes if w.id === du.workTypeId
    } yield (d.id, w.time)
    db.run(q.result)
  }

  def getDutys(weekId: Int): Future[Seq[Duty]] = {
    val q = for {
      w <- weeks if w.id === weekId
      da <- days if da.weekId === w.id
      d <- dutys if d.dayId === da.id
      c <- coworkers if c.id === d.coworkerId
      wo <- workTypes if wo.id === d.workTypeId
    } yield (d, c, wo)
    val k = db.run(q.result).map(x => {
      x.map(y => {
        val dutyTup = Duty.unapply(y._1).get
        new Duty(dutyTup._1, dutyTup._2, dutyTup._3, dutyTup._4, dutyTup._5) {
          override lazy val coworker: Coworker = y._2
          override lazy val workType: WorkType = y._3
        }
      })
    })
    k
  }

  def getWeekFromDay(day: Day): Future[Option[Week]] = {
    db.run(weeks.filter(w => w.id === day.weekId).result.headOption)
  }

  def getWeeks(projectId: Int): Future[Seq[Week]] = {
    db.run(weeks.filter(w => w.projectId === projectId).result)
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

  def updateDuty(duty: Duty): Future[Try[String]] = {
    val id = duty.id
    val q = for { d <- dutys if d.id === id } yield (d.coworkerId, d.workTypeId, d.locationId)
    val k = q.update((duty.coworkerId, duty.workTypeId, duty.locationId)).map(i => if(i > 0) Success("ok") else Failure{ new Exception("denne vagt findes ikke.")})
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

  def deleteDuty(dutyId: Int): Future[Try[String]] = {
    db.run(dutys.filter(d => d.id === dutyId).delete).map{ i =>
      if(i < 1) Failure(new Exception("Denne vagt blev ikke fundet")) else Success("ok")
    }
  }

  def deleteWeek(weekId: Int): Future[Try[String]] = {
    val q = for {
      day <- days if day.weekId === weekId
      d <- dutys if d.dayId === day.id
    } yield d

    val dut: Seq[Int] = Await.result(db.run(q.result), Duration.Inf).map(x => x.id)

    val con1 = Await.result(Future.sequence(dut.map(deleteDuty)), Duration.Inf)
      .map {
        case Success(_) => true
        case Failure(_) => false
      }.foldLeft(true)(_ && _)

    val con2 = if(con1) Await.result(db.run(days.filter(d => d.weekId === weekId).delete)
      .map(i => i == 7), Duration.Inf) else false

    if(con2)
    db.run(weeks.filter(w => w.id === weekId).delete)
      .map(i => if(i < 1) Failure(new Exception("Denne uge findes ikke.")) else Success("ok"))
    else Future{ Failure(new Exception("kunne ikke slette alle dage")) }
  }
}
