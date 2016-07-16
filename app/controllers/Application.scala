package weekplanning.controllers

import models.Coworker
import models.Week
import play.api.mvc
import play.api.mvc.{Action, Controller}
import service.DAL

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import weekplanning._

import scala.util.{Failure, Success}

class Application extends Controller with Secured {

  def test = Action {
/*    DAL.createUserSchema()
    DAL.createProjectSchema()
    DAL.createCollaboratesSchema()
    DAL.createCoworkerSchema()
    DAL.createWorkTypeSchema()
    DAL.createWeekSchema()
    DAL.createDaySchema()
    DAL.createDutySchema()*/

/*    DAL.addWeek(Week(0,32,2016,5))*/
    Ok("dfdf")
  }

  def index = withAuth { username => implicit request =>
    Ok(views.html.projects(Global.name, "Projects", Global.menu))
  }

  def notFound(str: String) = Action {
    Ok(views.html.projects(Global.name, "Projects", Global.menu))
  }

  def projects = withAuth { username => implicit request =>
    Ok(views.html.projects(Global.name, "Projects", Global.menu))
  }

}
