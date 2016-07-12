package models

import models.WeekDay.WeekDay
import slick.driver.PostgresDriver.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object WeekDay extends Enumeration {
  type WeekDay = Value
  val monday = Value(0)
  val tuesday = Value(1)
  val wednesday = Value(2)
  val thursday = Value(3)
  val friday = Value(4)
  val saturday = Value(5)
  val sunday = Value(6)

  implicit val myEnumMapper = MappedColumnType.base[WeekDay, Int](
    e => e.id,
    s => WeekDay(s)
  )

  val stringToWeekDay: Map[String, WeekDay] = Map(
      "monday" -> WeekDay.monday,
      "tuesday" -> WeekDay.thursday,
      "wednesday" -> WeekDay.wednesday,
      "thursday" -> WeekDay.thursday,
      "friday" -> WeekDay.friday,
      "saturday" -> WeekDay.saturday,
      "sunday" -> WeekDay.sunday
  )

  val weekDayToString = stringToWeekDay.map(_.swap)

  implicit val weekDayFormat = new Format[WeekDay] {
    def reads(json: JsValue) = JsSuccess(stringToWeekDay(json.as[String].value))
    def writes(week: WeekDay) = JsString(weekDayToString(week))
  }
}

case class Day(id: Int, weekId: Int, weekDay: WeekDay)

object Day{
  import WeekDay.weekDayFormat
  def tupled(tup:(Int, Int, WeekDay)) : Day = Day(tup._1, tup._2, tup._3)

  implicit val dayFormats = Json.format[Day]
}

class DayTableDef(tag: Tag) extends Table[Day](tag, "day") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def weekId = column[Int]("weekId")
  def weekDay = column[WeekDay]("weekDay")

  def fkToWeek = foreignKey("week_fk", weekId, TableQuery[WeekTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, weekId, weekDay) <>(Day.tupled, Day.unapply)
}
