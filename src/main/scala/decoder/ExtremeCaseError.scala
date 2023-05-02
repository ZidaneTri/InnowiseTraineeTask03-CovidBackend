package com.innowise
package decoder

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class ExtremeCaseError(country: String, errorDescription: String)

object ExtremeCaseError {
  implicit val extremeCaseErrorDecoder: Decoder[ExtremeCaseError] = deriveDecoder[ExtremeCaseError]
  implicit val extremeCaseErrorEncoder: Encoder[ExtremeCaseError] = deriveEncoder[ExtremeCaseError]
  implicit val extremeCaseErrorListDecoder: Decoder[List[ExtremeCaseError]] = Decoder.decodeList[ExtremeCaseError]
  implicit val extremeCaseErrorListEncoder: Encoder[List[ExtremeCaseError]] = Encoder.encodeList[ExtremeCaseError]
  implicit val extremeCaseErrorEntityEncoder: EntityEncoder[IO, ExtremeCaseError] = jsonEncoderOf[IO, ExtremeCaseError]
  implicit val extremeCaseErrorEntityDecoder: EntityDecoder[IO, ExtremeCaseError] = jsonOf[IO, ExtremeCaseError]
  implicit val extremeCaseErrorListEntityEncoder: EntityEncoder[IO, List[ExtremeCaseError]] = jsonEncoderOf[IO, List[ExtremeCaseError]]
  implicit val extremeCaseErrorListEntityDecoder: EntityDecoder[IO, List[ExtremeCaseError]] = jsonOf[IO, List[ExtremeCaseError]]
  
}