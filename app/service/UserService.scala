package service

import play.api.mvc.Result
import weekplanning.model.{User, UserTableDef}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import weekplanning.models.Level.Level

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

  def activateUser(username: String): Future[Try[String]] = {
    val user = Await.result(getUser(username), Duration.Inf)
    user match {
      case Some(x) => {
        val q = for {u <- users if u.username === username} yield (u.enabled)
        val k = q.update(true)
        val x = db.run(k).map(_ => Success("ok"))
        x
      }
      case None => Future { Failure { new Exception("denne bruger findes ikke") } }
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
