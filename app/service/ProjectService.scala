package service

import models._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import models.Level._
import weekplanning.model.{User, UserTableDef}

trait ProjectService {

  val db:JdbcProfile#Backend#Database
  val projects = TableQuery[ProjectTableDef]
  val collaborations = TableQuery[CollaboratesTabelDef]
  val users:TableQuery[UserTableDef]

  def getUser(username: String): Future[Option[User]]

  def createProjectSchema(): Unit = {
    db.run(projects.schema.create)
  }

  def createCollaboratesSchema(): Unit = {
    db.run(collaborations.schema.create)
  }

  def getProjectOwner(pId: Int): Future[Option[String]] = {
    db.run(collaborations.filter(_.projectId === pId).map(_.username).result.headOption)
  }

  def addCollaboration(projectId: Int, username: String, level: Level): Future[Try[Int]] = {
    Await.result(usersProjects(username), Duration.Inf)
      .map(_._1.id).find(_ == projectId)
      .map { _ =>
        Future {
          Failure {
            new Exception("Denne bruger samarbejder allerede på dette projekt.")
          }
        }
      }
      .getOrElse{
        db.run((collaborations returning collaborations
          .map( _.projectId )) += Collaborates(projectId, username, level)).map(x => Success(x))
      }
  }


  def userProjectsAndOwner(username:String) : Future[Seq[(Project, Level, String)]] = Future { //todo: lav til en query
   val lst = Await.result(usersProjects(username), Duration.Inf)
    lst.map{
      case(pro, level) => {
        val owner = Await.result(getProjectOwner(pro.id), Duration.Inf)
        owner match {
          case Some(x) => (pro, level, x)
          case _ => throw new Exception("project not found")
        }
      }
    }
  }

  def usersProjects(username:String): Future[Seq[(Project, Level)]] = {
    val query = for {
      p <- projects
      c <- collaborations if p.id === c.projectId
      u <- users if u.username === username && c.username === u.username
    } yield (p, c.level)
   db.run(query.result)
  }

  def createProject(projectName:String, owner:String):Future[Int] = {
    val usr = getUser(owner)
    Await.result(usr, Duration.Inf) match {
      case Some(x) => {
        val projectId:Int =
          Await.result(db.run((projects returning projects.map( _.id )) += Project(0, projectName))
            ,Duration.Inf)

        db.run(collaborations += Collaborates(projectId, owner, Level.Owner)).map(_ => projectId)
      }
      case _ => throw new Exception("username not found.")
    }
  }

}
