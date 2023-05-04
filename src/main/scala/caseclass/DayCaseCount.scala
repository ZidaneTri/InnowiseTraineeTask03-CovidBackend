package com.innowise
package caseclass

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

case class DayCaseCount(date: String, caseCount: Int)

object DayCaseCount {

  implicit val dayCaseCountDecoder: Decoder[DayCaseCount] = deriveDecoder[DayCaseCount]
  implicit val dayCaseCountEncoder: Encoder[DayCaseCount] = deriveEncoder[DayCaseCount]
  implicit val dayCaseCountListDecoder: Decoder[List[DayCaseCount]] = Decoder.decodeList[DayCaseCount]
  implicit val dayCaseCountListEncoder: Encoder[List[DayCaseCount]] = Encoder.encodeList[DayCaseCount]
  implicit val dayCaseCountEntityEncoder: EntityEncoder[IO, DayCaseCount] = jsonEncoderOf[IO, DayCaseCount]
  implicit val dayCaseCountEntityDecoder: EntityDecoder[IO, DayCaseCount] = jsonOf[IO, DayCaseCount]
  implicit val dayCaseCountListEntityEncoder: EntityEncoder[IO, List[DayCaseCount]] = jsonEncoderOf[IO, List[DayCaseCount]]
  implicit val dayCaseCountListEntityDecoder: EntityDecoder[IO, List[DayCaseCount]] = jsonOf[IO, List[DayCaseCount]]
  
}
