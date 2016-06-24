package weekplanning.models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class Project (id: Int, name:String)

object Project{
  def tupled(tup:(Int, String)) : Project = Project(tup._1, tup._2)

  implicit val projectFormats = Json.format[Project]
}

class ProjectTableDef(tag: Tag) extends Table[Project](tag, "project") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  override def * =
    (id, name) <>(Project.tupled, Project.unapply)
}

