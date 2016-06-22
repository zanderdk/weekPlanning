package service

import play.api.libs.json.{JsArray, Writes}

object JsonConverters {

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
