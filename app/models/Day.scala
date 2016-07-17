package models

import models.WeekDay.WeekDay
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import slick.driver.PostgresDriver.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import service.DAL

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object WeekDay extends Enumeration {
  type WeekDay = Value
  val monday = Value(1)
  val tuesday = Value(2)
  val wednesday = Value(3)
  val thursday = Value(4)
  val friday = Value(5)
  val saturday = Value(6)
  val sunday = Value(7)

  implicit val myEnumMapper = MappedColumnType.base[WeekDay, Int](
    e => e.id,
    s => WeekDay(s)
  )

  val stringToWeekDay: Map[String, WeekDay] = Map(
      "Mandag" -> WeekDay.monday,
      "Tirsdag" -> WeekDay.tuesday,
      "Ondsdag" -> WeekDay.wednesday,
      "Torsdag" -> WeekDay.thursday,
      "Fredag" -> WeekDay.friday,
      "Lørdag" -> WeekDay.saturday,
      "Søndag" -> WeekDay.sunday
  )

  val weekDayToString = stringToWeekDay.map(_.swap)

  implicit val weekDayFormat = new Format[WeekDay] {
    def reads(json: JsValue) = JsSuccess(stringToWeekDay(json.as[String].value))
    def writes(week: WeekDay) = JsString(weekDayToString(week))
  }
}

case class Day(id: Int, weekId: Int, weekDay: WeekDay) {

  lazy val week: Week = {
    Await.result(DAL.getWeekFromDay(this), Duration.Inf).get
  }

  lazy val dutys: Seq[Duty] = {
    Await.result(DAL.getDutys(weekId), Duration.Inf).filter(d => d.dayId == id)
  }

  lazy val jodaDate: DateTime = {
    val year = week.year
    val weekNo = week.weekNo
    val dt:DateTime = new DateTime()
    .withYear(year)
    .withWeekOfWeekyear(weekNo).withDayOfWeek(weekDay.id)
    dt
  }

  lazy val dayOfWeek = WeekDay.weekDayToString(weekDay)

  def date(): String = {
    val year = week.year
    val weekNo = week.weekNo
    val dt:DateTime = new DateTime()
    .withYear(year)
    .withWeekOfWeekyear(weekNo).withDayOfWeek(weekDay.id)
    val dateTimeFormatter:DateTimeFormatter  = DateTimeFormat.forPattern("dd/MM - yyyy")
    dateTimeFormatter.print(dt)
  }
}

object Day{
  import WeekDay.weekDayFormat
  def tupled(tup:(Int, Int, WeekDay)) : Day = Day(tup._1, tup._2, tup._3)

  def jsonUnapply(arg: Day): Option[(Int, Int, WeekDay, String, Seq[Duty])] = {
    Some(arg.id, arg.weekId, arg.weekDay, arg.date(), arg.dutys)
  }

  val dayWrits: Writes[Day] = (
  (JsPath \ "id").write[Int] and
  (JsPath \ "weekId").write[Int] and
  (JsPath \ "weekDay").write[WeekDay] and
  (JsPath \ "date").write[String] and
  (JsPath \ "dutys").write[Seq[Duty]]
  )(unlift(Day.jsonUnapply))


  val dayReads: Reads[Day] = (
  (JsPath \ "id").read[Int] and
  (JsPath \ "weekId").read[Int] and
  (JsPath \ "weekDay").read[WeekDay]
  )(Day.apply _)

  implicit val dayFormats: Format[Day] = Format(dayReads, dayWrits)

}

class DayTableDef(tag: Tag) extends Table[Day](tag, "day") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def weekId = column[Int]("weekId")
  def weekDay = column[WeekDay]("weekDay")

  def fkToWeek = foreignKey("week_fk", weekId, TableQuery[WeekTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, weekId, weekDay) <>(Day.tupled, Day.unapply)
}
