package weekplanning.controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import service.DAL
import weekplanning.Global
import weekplanning.model.User

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class Auth extends Controller {

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying (Global.loginErrorMessage,result => result match {
      case (username, password) => check(username, password)
    })
  )

  val registerForm = Form(
    tuple(
      "username" -> text,
      "password" -> text,
      "email" -> text
    ) verifying (Global.loginErrorMessage,result => result match {
      case (username, password, email) => checkRegister(username, password, email)
    })
  )

  def isUsernameTaken(username: String): Boolean = {
    Await.result(DAL.getUser(username), Duration.Inf).exists(_ => true)
  }

  def checkEmail(email: String) : Boolean = {
    isValid(email) && !Await.result(DAL.listAllUsers, Duration.Inf).exists(usr => usr.email == email)
  }

  def isValid(email: String): Boolean =
    if("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email).isEmpty)false else true

  def checkRegister(username: String, password:String, email: String) :Boolean = {
    if(checkEmail(email))
    {
      !isUsernameTaken(username)
    } else false
  }

  def signin = Action {
    Ok(views.html.signin(Global.name))
  }

  def checkUsernameAndEmail(username: String, email:String) = Action {
    if(isUsernameTaken(username)) Ok(Global.usernameTakenMessage) else {
      if(!isValid(email)) Ok(Global.invalidEmailMessage) else {
        if(checkEmail(email)) Ok("ok") else {Ok(Global.emailInUseMessage)}
      }
    }
  }

  def register = Action { implicit request =>
     registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.signin(Global.name)),
      user => {
        val us = (user._1.toLowerCase, user._2, user._3.toLowerCase)
        val usr = Function.tupled( User.apply(_ : String, _ : String, _ : String, true, false) )(us)
        Await.result(DAL.addUser(usr), Duration.Inf) match {
          case Failure(ex) => Ok(ex.getCause.getMessage)
          case Success(x) =>
            Redirect(routes.Application.index).withSession(Security.username -> user._1)
        }
      }
    )
  }

  def check(username: String, password: String) = {
    Await.result(DAL.getUser(username), Duration.Inf).exists(x => x.password == password && x.enabled)
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.signin(Global.name)),
      user => Redirect(routes.Application.index).withSession(Security.username -> user._1)
    )
  }

  def signinInitCheck = Action {implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Ok(formWithErrors.errors.head.messages.head),
      user => Ok("ok")
    )
  }

  def logout = Action {
    Redirect(routes.Auth.signin).withNewSession
  }

}
