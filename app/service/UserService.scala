package service

import weekplanning.model.{User, UserTableDef}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

trait UserService {

  val db:JdbcProfile#Backend#Database
  val users = TableQuery[UserTableDef]

  def createUserSchema(): Unit ={
    db.run(users.schema.create)
  }

  def addUser(user: User): Future[Try[String]] = {
    db.run(users += user).map(res => Success("ok")).recover {
      case ex: Exception => Failure(ex)
    }
  }

  def deleteUser(username: String): Future[Try[Int]] = {
    db.run(users.filter(_.username === username).delete).map(res => Success(res)).recover{
      case ex: Exception => Failure(ex)
    }
  }

  def getUser(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }
  def getUser(username: String): Future[Option[User]] = {
    db.run(users.filter(_.username === username).result.headOption)
  }

  def listAllUsers: Future[Seq[User]] = {
    db.run(users.result)
  }
}
