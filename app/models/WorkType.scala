package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import weekplanning.models.ProjectTableDef

case class WorkType(id: Int, projectId: Int, name: String, time: Double)

object WorkType{
  def tupled(tup:(Int, Int, String, Double)) : WorkType = WorkType(tup._1, tup._2, tup._3, tup._4)

  implicit val workTypeFormats = Json.format[WorkType]
}
class WorkTypeTableDef(tag: Tag) extends Table[WorkType](tag, "worktype") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def name = column[String]("name")
  def time = column[Double]("time")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, projectId, name, time) <>(WorkType.tupled, WorkType.unapply)
}
