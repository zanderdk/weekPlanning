package models

import play.api.libs.json.Json
import play.twirl.api.Html
import service.DAL
import slick.driver.PostgresDriver.api._
import weekplanning.models.ProjectTableDef

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Coworker(id: Int, projectId: Int, time: Double, name: String) {

  def dutys = {
    Await.result(DAL.getCoworkerDutys(id), Duration.Inf)
  }

  def norm(week: Week, days:Seq[Day]): String = {
    val year = week.year
    val weekNo = week.weekNo

    val firstDay = days.find( d =>
      d.jodaDate.getYear + 1 == year
      && d.jodaDate.getMonthOfYear == 12
      && d.jodaDate.getDayOfMonth == 31
    ).getOrElse{
      days.filter(d =>
        d.jodaDate.getYear == year
      ).reduceLeft[Day]{ case(d:Day, d1:Day) => if(d.jodaDate.getDayOfYear() < d.jodaDate.getDayOfYear()) d else d1 }
    }

    val lastDay = week.days(6)

    val daysBetween = days.filter(d => d.jodaDate.isAfter(firstDay.jodaDate) && d.jodaDate.isBefore(lastDay.jodaDate))
        .:+(firstDay).:+(lastDay)

    val timePerDay = time / 5

    (daysBetween.filter(d => d.weekDay.id <= 5).length * timePerDay).toString.take(5)
  }

  def workTimeForWeek(week: Week): String = {
    week.days.view.flatMap(_.dutys).filter(_.coworkerId == id)
      .map(_.workType.time).sum.toString.take(5)
  }

  def statusForDay(day: Day): Seq[(String, String)] = {
    val dutys = day.dutys
    if(dutys.isEmpty) Seq(("Lukket", "#fff")) else {
      val lst = dutys.filter(d => d.coworkerId == id).map(d => (d.workType.name, d.location.color))
      val ll = lst.map{ case (x, y) => (x, "#" + y) }
      ll
    }
  }

  def calcTimeToDate( day: Day, week:Week, days: Seq[Day] ): String = {
    val year = week.year
    val weekNo = week.weekNo

    val firstDay = days.find( d =>
      d.jodaDate.getYear + 1 == year
      && d.jodaDate.getMonthOfYear == 12
      && d.jodaDate.getDayOfMonth == 31
    ).getOrElse{
      days.filter(d =>
        d.jodaDate.getYear == year
      ).reduceLeft[Day]{ case(d:Day, d1:Day) => if(d.jodaDate.getDayOfYear() < d.jodaDate.getDayOfYear()) d else d1 }
    }

    val lastDay = day

    val daysBetween = days.filter(d => d.jodaDate.isAfter(firstDay.jodaDate) && d.jodaDate.isBefore(lastDay.jodaDate))
        .:+(firstDay).:+(lastDay).map(_.id)

    val dutyTimes = Await.result(DAL.getAllDutysForCoworker(week.projectId, id), Duration.Inf)

    val time = dutyTimes.view.filter(x => daysBetween.contains(x._1))
      .map(_._2).sum

    time.toString.take(5)
  }

  def totalWorkTime() : String = {
    dutys.map(_.workType.time).sum.toString.take(5)
  }


}

object Coworker{
  def tupled(tup:(Int, Int, Double, String)) : Coworker = Coworker(tup._1, tup._2, tup._3, tup._4)

  implicit val coworkerFormats = Json.format[Coworker]
}
class CoworkerTableDef(tag: Tag) extends Table[Coworker](tag, "coworker") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def projectId = column[Int]("projectId")
  def time = column[Double]("time")
  def name = column[String]("name")

  def fkToProject = foreignKey("project_fk", projectId, TableQuery[ProjectTableDef])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  override def * =
    (id, projectId, time, name) <>(Coworker.tupled, Coworker.unapply)
}
