package service

import play.api.data.validation.ValidationError
import play.api.libs.json._

object JsonConverters {

  implicit def tuple2Writes[A, B](
   implicit aWrites: Writes[A],
   bWrites: Writes[B]): Writes[Tuple2[A, B]] =
    new Writes[Tuple2[A, B]] {
      def writes(tuple: Tuple2[A, B]) = {
        JsArray(Seq(
            aWrites.writes(tuple._1),
            bWrites.writes(tuple._2)
          )
        )
      }
    }

 implicit def tuple2Reads[A, B](implicit aReads: Reads[A], bReads: Reads[B]):    Reads[Tuple2[A, B]] = Reads[Tuple2[A, B]] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
     b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of three elements"))))
  }

  implicit def tuple3Writes[A, B, C](
   implicit aWrites: Writes[A],
   bWrites: Writes[B],
   cWrites: Writes[C] ): Writes[Tuple3[A, B, C]] =
    new Writes[Tuple3[A, B, C]] {
      def writes(tuple: Tuple3[A, B, C]) = {
        JsArray(Seq(
            aWrites.writes(tuple._1),
            bWrites.writes(tuple._2),
            cWrites.writes(tuple._3)
          )
        )
      }
    }
}
