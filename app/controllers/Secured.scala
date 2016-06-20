package weekplanning.controllers

import play.api.mvc._
import service.DAL
import weekplanning.model.User

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.signin)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
    Await.result(DAL.getUser(username), Duration.Inf).map { user =>
      f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }

}
