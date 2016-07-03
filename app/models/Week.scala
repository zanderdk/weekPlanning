package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class Week(id: Int, ProjectId: Int, year: Int, weekNo: Int) {
  def dayes = ???
}

object Week{
  def tupled(tup:(Int, Int, Int, Int)) : Week = Week(tup._1, tup._2, tup._3, tup._4)

  implicit val weekFormats = Json.format[Week] //todo implement with days
}
class WeekTableDef(tag: Tag) extends Table[Week](tag, "week") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def year = column[Int]("year")
  def weekNo = column[Int]("year")

  override def * =
    (id, projectId, year, weekNo) <>(Week.tupled, Week.unapply)
}
