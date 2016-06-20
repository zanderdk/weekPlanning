package weekplanning.model

import slick.driver.PostgresDriver.api._

case class User(username: String, password: String, email: String, enabled:Boolean = true, admin:Boolean = false)

class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

  def username = column[String]("username", O.PrimaryKey)
  def password = column[String]("password")
  def email = column[String]("email")
  def enabled = column[Boolean]("enabled", O.Default(true))
  def admin = column[Boolean]("admin", O.Default(false))


  override def * =
    (username, password, email, enabled, admin) <>(User.tupled, User.unapply)
}


