package weekplanning.controllers

import play.api.libs.json.Json
import play.api.mvc.Controller
import service.DAL
import weekplanning.controllers.Secured
import weekplanning.model.User
import scala.concurrent.Await
import scala.concurrent.duration.Duration


class UserController extends Controller with Secured {

  def searchUsers() = withAuth { username => implicit request =>
    val k = request.queryString.get("name")
    val name: String = k.map(_.headOption).getOrElse( Some("") ).get
    if(name.length > 2) {
      val users = Await.result(DAL.listAllUsers, Duration.Inf) //todo Inf
        .filter(u => u.username.toLowerCase.indexOf(name.toLowerCase) != -1 && u.username != username)
        .map(u => u.copy(password = "")).take(5)
      Ok(Json.toJson(users))
    } else {
      val users: Seq[User] = Seq()
      Ok(Json.toJson(users))
    }
  }

  def getVisability(id: Int) = withAuth { username => implicit request =>
    val viability = Await.result(DAL.usersProjects(username), Duration.Inf)
        .find(p => p._1.id == id).map(x => x._2)
    val str = viability match {
      case Some(x) => x.toString
      case _=> "Dette projekt findes ikke"
    }
    Ok(str)
  }

  def getUserById(id: Int) = withAuth { username => implicit request =>
    Await.result(DAL.getUser(id), Duration.Inf).map(u => u.copy(password = ""))
      .map(u => Ok(Json.toJson(u)))
      .getOrElse(Ok(""))
  }

}
