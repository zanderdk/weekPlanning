package weekplanning.models

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

  val stringToLevel: Map[String, Level] = Map(
      "Owner" -> Level.Owner,
      "Write" -> Level.Write,
      "Read" -> Level.Read
    )

  val levelToString = stringToLevel.map(_.swap)

}


case class Collaborates (projectId:Int, userId:Int, level:Level.Level)

class CollaboratesTabelDef(tag: Tag) extends Table[Collaborates](tag, "collaborates") {

  import Level._

  def projectId = column[Int]("id")
  def userId = column[Int]("userId")
  def level = column[Level]("level")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def fkToUser = foreignKey("user_fk", userId, TableQuery[UserTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def pk = primaryKey("pk", (projectId, userId))

  override def * =
    (projectId, userId, level) <>(Collaborates.tupled, Collaborates.unapply)
}
