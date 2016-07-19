package service

import models._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}


trait LocationService {

  val db:JdbcProfile#Backend#Database
  val locations = TableQuery[LocationTableDef]

  def creatLocationSchema() = {
    db.run(locations.schema.create)
  }

  def getLocations(projectId: Int): Future[Seq[Location]] = {
    db.run(locations.filter(l => l.projectId === projectId).result)
  }

  def getLocation(locationId: Int): Future[Option[Location]] = {
    db.run(locations.filter(l => l.id === locationId).result.headOption)
  }

  def addLocation(location: Location): Future[Try[String]] = {
    db.run(locations += location).map(_ => Success("ok")).recover{
      case ex:Exception => Failure(ex)
    }
  }

  def deleteLocation(locationId: Int): Future[Try[String]] = {
    db.run(locations.filter(l => l.id === locationId).delete).map(_ => Success("ok"))
  }

  def updateLocation(location: Location): Future[Try[String]] = {
    val q = for {
      l <- locations if l.id === location.id
    } yield (l.name, l.color)
    db.run(q.update(location.name, location.color)).map(_ => Success("ok")).recover{
      case ex:Exception => Failure(ex)
    }
  }

}
