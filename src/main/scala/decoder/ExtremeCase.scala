package com.innowise
package decoder

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

abstract class ExtremeCase {
  
  val country: String

}

object ExtremeCase {

  /*implicit val extremeCaseDecoder: Decoder[ExtremeCase] = deriveDecoder[ExtremeCase]
  implicit val extremeCaseEncoder: Encoder[ExtremeCase] = deriveEncoder[ExtremeCase]
  implicit val extremeCaseListDecoder: Decoder[List[ExtremeCase]] = Decoder.decodeList[ExtremeCase]
  implicit val extremeCaseListEncoder: Encoder[List[ExtremeCase]] = Encoder.encodeList[ExtremeCase]
  implicit val extremeCaseEntityEncoder: EntityEncoder[IO, ExtremeCase] = jsonEncoderOf[IO, ExtremeCase]
  implicit val extremeCaseEntityDecoder: EntityDecoder[IO, ExtremeCase] = jsonOf[IO, ExtremeCase]
  implicit val extremeCaseListEntityEncoder: EntityEncoder[IO, List[ExtremeCase]] = jsonEncoderOf[IO, List[ExtremeCase]]
  implicit val extremeCaseListEntityDecoder: EntityDecoder[IO, List[ExtremeCase]] = jsonOf[IO, List[ExtremeCase]]*/

}
