package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import service.DAL
import slick.driver.PostgresDriver.api._
import weekplanning.models.ProjectTableDef

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Week(id: Int, projectId: Int, year: Int, weekNo: Int) {
  lazy val days: Seq[Day] = Await.result(DAL.getDays(id), Duration.Inf)
}

object Week{
  def tupled(tup:(Int, Int, Int, Int)) : Week = Week(tup._1, tup._2, tup._3, tup._4)

  def jsonUnapply(arg: Week): Option[(Int, Int, Int, Int)] = {
    Some(arg.id, arg.projectId, arg.year, arg.weekNo)
  }

  val weekWrits: Writes[Week] = (
  (JsPath \ "id").write[Int] and
  (JsPath \ "projectId").write[Int] and
  (JsPath \ "year").write[Int] and
  (JsPath \ "weekNo").write[Int]
  )(unlift(Week.jsonUnapply))

  val weekReads: Reads[Week] = (
  (JsPath \ "id").read[Int] and
  (JsPath \ "projectId").read[Int] and
  (JsPath \ "year").read[Int] and
  (JsPath \ "weekNo").read[Int]
  )(Week.apply _)

  implicit val weekFormats: Format[Week] = Format(weekReads, weekWrits)
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
