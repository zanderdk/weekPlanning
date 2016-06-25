package service

import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import weekplanning.model.{User, UserTableDef}
import weekplanning.models.Level.Level
import weekplanning.models._

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

  def getProjectOwner(pId: Int): Future[Option[User]] = {
     val query = for {
        p <- projects
        c <- collaborations if p.id === c.projectId && c.projectId === p.id
        u <- users if c.userId === u.id && c.level === Level.Owner
      } yield u
     db.run(query.result.headOption)
    //db.run(collaborations.filter(_.projectId === pId).map(_.username).result.headOption)
  }

  def addCollaboration(projectId: Int, username: String, level: Level): Future[Try[Int]] = {
    val usr = Await.result(getUser(username), Duration.Inf)
    usr match {
      case Some(x) =>
        Await.result(usersProjects(username), Duration.Inf)
          .map(_._1.id).find(_ == projectId)
          .map { _ =>
            Future {
              Failure {
                new Exception("Denne bruger samarbejder allerede på dette projekt.")
              }
            }
          }
          .getOrElse {
            db.run((collaborations returning collaborations
              .map(_.projectId)) += Collaborates(projectId, x.id, level)).map(x => Success(x))
          }
      case _ => Future { Failure { new Exception("error") } }
    }
  }


  def userProjectsAndOwner(username:String) : Future[Seq[(Project, Level, String)]] = Future { //todo: lav til en query
   val lst = Await.result(usersProjects(username), Duration.Inf)
    lst.map{
      case(pro, level) => {
        val owner = Await.result(getProjectOwner(pro.id), Duration.Inf)
        owner match {
          case Some(x) => (pro, level, x.username)
          case _ => throw new Exception("project not found")
        }
      }
    }
  }

  def deleteAllColaboratorsForProject(id: Int): Future[Try[String]] = {
    db.run(collaborations.filter(c => c.projectId === id).delete).map(_ => Success("ok"))
  }

  def updateProjectCollaborators(id: Int, lst:Seq[(User, Level)]) :
  Future[Try[String]] = {
    val colabs = lst.map(y => Collaborates(id, y._1.id, y._2))
    db.run((collaborations.filter(_.projectId === id).delete).andThen(collaborations ++= colabs)).map(_ => Success("ok"))
  }

  def getCollaborators(id: Int): Future[Seq[(User, Level)]] = {
     val query = for {
      p <- projects
      c <- collaborations if p.id === c.projectId && c.projectId === id
      u <- users if c.userId === u.id
    } yield (u, c.level)
   db.run(query.result)
  }

  def usersProjects(username:String): Future[Seq[(Project, Level)]] = {
    val x = Await.result(getUser(username), Duration.Inf)
    val query = for {
      p <- projects
      c <- collaborations if p.id === c.projectId
      u <- users if u.username === username && c.userId === u.id
    } yield (p, c.level)
   db.run(query.result)
  }

  def deleteProject(id: Int): Future[Try[Int]] = {
    db.run(projects.filter(_.id === id).delete).map(res => Success(res)).recover{
      case ex: Exception => Failure(ex)
    }
  }

  def updateProject(project: Project): Future[Try[Int]] = {
    val q = for { p <- projects if p.id === project.id } yield p.name
    val updateAction = q.update(project.name)
    try{ db.run(updateAction).map(Success(_)) } catch {
      case e:Exception => Future { Failure { e } }
    }
  }

  def createProject(projectName:String, owner:String):Future[Try[Int]] = {
    val usr = getUser(owner)
    lazy val res =  {
        Await.result(usr, Duration.Inf) match {
        case Some(x) => {
          val projectId:Int =
            Await.result(db.run((projects returning projects.map( _.id )) += Project(0, projectName))
              ,Duration.Inf)

          db.run(collaborations += Collaborates(projectId, x.id, Level.Owner)).map(_ => projectId)
        }
        case _ => throw new Exception("username not found.")
      }
    }

    val x = Await.result(usersProjects(owner), Duration.Inf).find(p => p._1.name == projectName && p._2 == Level.Owner )

    x match {
      case Some(_) => Future{ Failure { new Exception("Du har allerede et projekt med dette navn.") } }
      case _ => res.map(x => Success(x))
    }

    }

}
