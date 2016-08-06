package service

import scala.concurrent.Future
import scala.util.Try
import play.api.libs.mailer._
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import com.roundeights.hasher.Implicits._
import weekplanning.Global

import scala.language.postfixOps


@Singleton
class EmailService @Inject() (mailerClient: MailerClient) {

  val senderMail = "auto@vagtplanen.tk"
  val pass = "somepass"
  val secret = "dfgdhdsfdfghjsdfzgbf324456edhgfbzdf"

  def sendConfirmEmail(username: String, email: String) :Future[Try[String]] = {
    Future {
      val mail = Email(
        "Bekraft email til vagtplanen.tk",
        ("<" + email + ">"),
        Seq(("<" + email + ">")),
        bodyText = Some("Vær vendelig at bekræfte din email adresse ved at trykke på linket under.\n" +
          "https://" + Global.host + "/confirm?username=" + username + "&code=" + genConfirmCode(username)
        )
      )
      val k = Try {
        mailerClient.send(mail)
      }
      k
    }
  }


  def genConfirmCode(username: String): String = {
    (secret + username).sha512
  }

}
