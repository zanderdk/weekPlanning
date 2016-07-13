package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import weekplanning.models.ProjectTableDef

case class Week(id: Int, projectId: Int, year: Int, weekNo: Int)

object Week{
  def tupled(tup:(Int, Int, Int, Int)) : Week = Week(tup._1, tup._2, tup._3, tup._4)

  implicit val weekFormats = Json.format[Week]
}
class WeekTableDef(tag: Tag) extends Table[Week](tag, "week") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def year = column[Int]("year")
  def weekNo = column[Int]("weekNo")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, projectId, year, weekNo) <>(Week.tupled, Week.unapply)
}
