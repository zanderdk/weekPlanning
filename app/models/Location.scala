package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import weekplanning.models.ProjectTableDef

case class Location(id: Int, projectId: Int, name: String, color:String)

object Location {

  def tupled(tup:(Int, Int, String, String)) = Function.tupled(Location.apply _)(tup)

  implicit val locationFormats = Json.format[Location]
}

class LocationTableDef(tag: Tag) extends Table[Location](tag, "location") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def name = column[String]("name")
  def color = column[String]("color")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, projectId, name, color) <>(Location.tupled, Location.unapply)
}
