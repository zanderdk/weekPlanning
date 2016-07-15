package models

import models.WeekDay.WeekDay
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import slick.driver.PostgresDriver.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import service.DAL
import models.Day.dayFormats
import models.WorkType.workTypeFormats

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Duty (id: Int, dayId: Int, coworkerId: Int, workTypeId: Int) {
  lazy val day: Day = Await.result(DAL.getDayFromDuty(dayId), Duration.Inf).get
  lazy val coworker: Coworker = Await.result(DAL.getCoworkerFromDuty(coworkerId), Duration.Inf).get
  lazy val workType: WorkType = Await.result(DAL.getWorkTypeFromDuty(workTypeId), Duration.Inf).get
}

object Duty {
  def tupled(tup:(Int, Int, Int, Int)) : Duty = Duty(tup._1, tup._2, tup._3, tup._4)

  def jsonUnapply(arg: Duty): Option[(Int, Int, Int, Int, Coworker, WorkType)] = {
    Some(arg.id, arg.dayId, arg.coworkerId, arg.workTypeId, arg.coworker, arg.workType)
  }

  val dutyReads: Reads[Duty] = (
  (JsPath \ "id").read[Int] and
  (JsPath \ "dayId").read[Int] and
  (JsPath \ "coworkerId").read[Int] and
  (JsPath \ "workTypeId").read[Int]
  )(Duty.apply _)

  val dutyWrits: Writes[Duty] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "dayId").write[Int] and
    (JsPath \ "coworkerId").write[Int] and
    (JsPath \ "workTypeId").write[Int] and
    (JsPath \ "coworker").write[Coworker] and
    (JsPath \ "workType").write[WorkType]
  )(unlift(Duty.jsonUnapply))

  implicit val dutyFormats: Format[Duty] = Format(dutyReads, dutyWrits)
}

class DutyTableDef(tag: Tag) extends Table[Duty](tag, "duty") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def dayId = column[Int]("dayId")
  def coworkerId = column[Int]("coworkerId")
  def workTypeId = column[Int]("workTypeId")

  def fkToDay = foreignKey("day_fk", dayId, TableQuery[DayTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def fkToCoworker = foreignKey("coworker_fk", coworkerId, TableQuery[CoworkerTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def fkToWorkType = foreignKey("work_type_fk", workTypeId, TableQuery[WorkTypeTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, dayId, coworkerId, workTypeId) <>(Duty.tupled, Duty.unapply)
}
