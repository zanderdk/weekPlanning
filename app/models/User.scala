package weekplanning.model

import slick.driver.PostgresDriver.api._

case class User(username: String, password: String, email: String, enabled:Boolean = true, admin:Boolean = false, id: Int = 0)

class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def password = column[String]("password")
  def email = column[String]("email")
  def enabled = column[Boolean]("enabled", O.Default(true))
  def admin = column[Boolean]("admin", O.Default(false))


  override def * =
    (username, password, email, enabled, admin, id) <>(User.tupled, User.unapply)
}


