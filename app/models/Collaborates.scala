package models

import models.Level.Level
import slick.driver.PostgresDriver.api._
import weekplanning.model.UserTableDef


object Level extends Enumeration {
  type Level = Value
  val Owner = Value(2)
  val Write = Value(1)
  val Read = Value(0)

  implicit val myEnumMapper = MappedColumnType.base[Level, Int](
    e => e.id,
    s => Level(s)
  )
}


case class Collaborates (projectId:Int, userName:String, level:Level)

class CollaboratesTabelDef(tag: Tag) extends Table[Collaborates](tag, "collaborates") {

  import Level._

  def projectId = column[Int]("id")
  def username = column[String]("username")
  def level = column[Level]("level")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def fkToUser = foreignKey("user_fk", username, TableQuery[UserTableDef])(_.username, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def pk = primaryKey("pk", (projectId, username))

  override def * =
    (projectId, username, level) <>(Collaborates.tupled, Collaborates.unapply)
}
