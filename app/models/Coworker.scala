package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class Coworker(id: Int, ProjectId: Int, name: String)

object Coworker{
  def tupled(tup:(Int, Int, String)) : Coworker = Coworker(tup._1, tup._2, tup._3)

  implicit val coworkerFormats = Json.format[Coworker] //todo implement with days
}
class CoworkerTableDef(tag: Tag) extends Table[Coworker](tag, "coworker") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def name = column[String]("name")

  override def * =
    (id, projectId, name) <>(Coworker.tupled, Coworker.unapply)
}
