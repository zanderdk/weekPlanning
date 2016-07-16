package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import weekplanning.models.ProjectTableDef

case class Coworker(id: Int, ProjectId: Int, time: Double, name: String)

object Coworker{
  def tupled(tup:(Int, Int, Double, String)) : Coworker = Coworker(tup._1, tup._2, tup._3, tup._4)

  implicit val coworkerFormats = Json.format[Coworker] //todo implement with days
}
class CoworkerTableDef(tag: Tag) extends Table[Coworker](tag, "coworker") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def time = column[Double]("time")
  def name = column[String]("name")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, projectId, time, name) <>(Coworker.tupled, Coworker.unapply)
}
