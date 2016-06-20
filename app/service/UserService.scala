package service

import weekplanning.model.{User, UserTableDef}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UserService {

  val db:JdbcProfile#Backend#Database
  val users = TableQuery[UserTableDef]

  def createUserSchema(): Unit ={
    db.run(users.schema.create)
  }

  def addUser(user: User): Future[String] = {
    db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def deleteUser(username: String): Future[Int] = {
    db.run(users.filter(_.username === username).delete)
  }

  def getUser(username: String): Future[Option[User]] = {
    db.run(users.filter(_.username === username).result.headOption)
  }

  def listAllUsers: Future[Seq[User]] = {
    db.run(users.result)
  }
}
