package service

import models.{Coworker, CoworkerTableDef, DutyTableDef, WorkType}
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
  val coworkers = TableQuery[CoworkerTableDef]
  val dutys:TableQuery[DutyTableDef]

  def createCoworkerSchema(): Unit ={
    db.run(coworkers.schema.create)
  }


  def updateCoworker(projectId: Int, coworker: Coworker): Future[Try[String]] = {
    val q = for { c <- coworkers if c.name === coworker.name && c.projectId === projectId } yield (c.name, c.time)
    val k = q.update(coworker.name, coworker.time).map(i => if(i > 0) Success("ok") else Failure{ new Exception("inger bruger med det navn")})
    db.run(k)
  }

  def deleteCoworker(projectId: Int, name:String): Future[Try[String]] = {
    val coworker = Await.result(getCoworker(projectId, name), Duration.Inf)

    coworker match {
      case None => Future{ Failure(new Exception("denne medarbejer findes ikke")) }
      case Some(cow) => {
        Await.result(db.run(dutys.filter(d => d.coworkerId === cow.id).delete), Duration.Inf)
        db.run(coworkers.filter(c => c.projectId === projectId && c.name === name).delete)
          .map(i => if(i > 0) Success("ok") else Failure(new Exception("ingen person med dette navn.")))
      }
    }
  }

  def getCoworker(projectId: Int, name: String): Future[Option[Coworker]] = {
    db.run(coworkers.filter(c => c.projectId === projectId && c.name === name).result.headOption)
  }

  def getCoworkers(projectId: Int): Future[Seq[Coworker]] = {
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
