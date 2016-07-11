package service

import models.{Coworker, CoworkerTableDef}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import weekplanning.model.{User, UserTableDef}
import weekplanning.models.Level.Level
import weekplanning.models._

trait CoworkerService {

  val db:JdbcProfile#Backend#Database
  val projects: TableQuery[ProjectTableDef]
  val collaborations: TableQuery[CollaboratesTabelDef]
  val users:TableQuery[UserTableDef]
  val coworkers = TableQuery[CoworkerTableDef]

  def createCoworkerSchema(): Unit ={
    db.run(coworkers.schema.create)
  }

  def updateCoworker(projectId: Int, oldName: String, name: String): Future[Try[String]] = {
    val q = for { c <- coworkers if c.name === oldName } yield c.name
    val k = q.update(name).map(i => if(i > 0) Success("ok") else Failure{ new Exception("inger bruger med det navn")})
    db.run(k)
  }

  def deleteCoworker(projectId: Int, name:String): Future[Try[String]] = {
    db.run(coworkers.filter(c => c.projectId === projectId && c.name === name).delete)
      .map(i => if(i > 0) Success("ok") else Failure(new Exception("ingen person med dette navn.")))
  }

  def getCoworker(projectId: Int): Future[Seq[Coworker]] = {
     val query = for {
      c <- coworkers if c.projectId === projectId
    } yield c
   db.run(query.result)
  }

  def addCoworker(coworker: Coworker): Future[Try[String]] = {
    db.run(coworkers += coworker).map(res => Success("ok")).recover {
      case ex: Exception => Failure(ex)
    }
  }

}
