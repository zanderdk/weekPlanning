package weekplanning.controllers

import javax.inject.Inject

import models.Coworker
import models.Week
import play.api.mvc
import play.api.mvc.{Action, Controller}
import service.{DAL, EmailService}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import weekplanning._

import scala.util.{Failure, Success}

class Application @Inject() (emailService: EmailService) extends Controller with Secured {

  def test = Action {
/*    DAL.createUserSchema()
    DAL.createProjectSchema()
    DAL.createCollaboratesSchema()
    DAL.createCoworkerSchema()
    DAL.createWorkTypeSchema()
    DAL.createWeekSchema()
    DAL.createDaySchema()
    DAL.creatLocationSchema()
    DAL.createDutySchema()*/



/*    DAL.addWeek(Week(0,32,2016,5))*/
    val x = Await.result(emailService.sendConfirmEmail("zander", "moonfrogdk@gmail.com"), Duration.Inf)
    x match {
      case Success(y) => Ok(y)
      case Failure(ex) => Ok(ex.getCause.getMessage)
    }
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
