package service

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

class DAL(dbName: String) extends
  UserService with
  ProjectService with
  CoworkerService with
  ScheduleService with
  WorkTypeService {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](dbName)(Play.current)
  override val db = dbConfig.db

}

object DAL extends DAL("default")



