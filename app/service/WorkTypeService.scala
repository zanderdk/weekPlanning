package service

import models.{DutyTableDef, WorkType, WorkTypeTableDef}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

trait WorkTypeService {

  //todo flyt constante strings

  val db:JdbcProfile#Backend#Database
  val workTypes = TableQuery[WorkTypeTableDef]
  val dutys:TableQuery[DutyTableDef]

  def createWorkTypeSchema(): Unit ={
    db.run(workTypes.schema.create)
  }

  def getWorkType(id: Int): Future[Option[WorkType]] = {
    db.run(workTypes.filter(w => w.id === id).result.headOption)
  }

  def updateWorkType(work:WorkType): Future[Try[String]] = {
    val id = work.id
    val q = for { w <- workTypes if w.id === id } yield (w.name, w.time)
    val k = q.update((work.name, work.time)).map(i => if(i > 0) Success("ok") else Failure{ new Exception("denne vagt type findes ikke.")})
    db.run(k)
  }

  def getWorkTypeProjectId(id: Int): Future[Option[Int]] = {
    val q = for {
      w <- workTypes if w.id === id
    } yield w.projectId
    db.run(q.result.headOption)
  }

  def addWorkType(workType: WorkType): Future[Try[String]] = {
    db.run(workTypes += workType).map( x => Success("ok") ).recover{
      case ex:Exception => Failure (ex)
    }
  }

  def getWorkTypes(projectId: Int): Future[Seq[WorkType]] = {
    val query = for {
      w <- workTypes if w.projectId === projectId
    } yield w
    db.run(query.result)
  }

  def deleteWorkType(id: Int): Future[Try[String]] = {
    Await.result(db.run(dutys.filter(d => d.workTypeId === id).delete), Duration.Inf)
    db.run(workTypes.filter(w => w.id === id).delete)
      .map(i => if(i < 1) Failure(new Exception("Denne vagt type findes ikke")) else Success("ok") )
  }

}


