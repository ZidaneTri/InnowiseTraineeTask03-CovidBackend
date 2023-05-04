package com.innowise
package caseclass

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

case class ExtremeCaseValue(country: String, minCaseCount: Int, minCaseDates: List[String], maxCaseCount: Int, maxCaseDates: List[String])


object ExtremeCaseValue {
  implicit val extremeCaseValueDecoder: Decoder[ExtremeCaseValue] = deriveDecoder[ExtremeCaseValue]
  implicit val extremeCaseValueEncoder: Encoder[ExtremeCaseValue] = deriveEncoder[ExtremeCaseValue]
  implicit val extremeCaseValueListDecoder: Decoder[List[ExtremeCaseValue]] = Decoder.decodeList[ExtremeCaseValue]
  implicit val extremeCaseValueListEncoder: Encoder[List[ExtremeCaseValue]] = Encoder.encodeList[ExtremeCaseValue]
  implicit val extremeCaseValueEntityEncoder: EntityEncoder[IO, ExtremeCaseValue] = jsonEncoderOf[IO, ExtremeCaseValue]
  implicit val extremeCaseValueEntityDecoder: EntityDecoder[IO, ExtremeCaseValue] = jsonOf[IO, ExtremeCaseValue]
  implicit val extremeCaseValueListEntityEncoder: EntityEncoder[IO, List[ExtremeCaseValue]] = jsonEncoderOf[IO, List[ExtremeCaseValue]]
  implicit val extremeCaseValueListEntityDecoder: EntityDecoder[IO, List[ExtremeCaseValue]] = jsonOf[IO, List[ExtremeCaseValue]]
 implicit def eitherEncoder[ExtremeCaseValue, ExtremeCaseError](implicit encodeA: Encoder[ExtremeCaseValue], encodeB: Encoder[ExtremeCaseError]
                                                                ): Encoder[Either[ExtremeCaseValue, ExtremeCaseError]] = {
    case Left(a) => encodeA(a)
    case Right(b) => encodeB(b)
  }

  implicit val eitherListEncoder: Encoder[List[Either[ExtremeCaseValue, ExtremeCaseError]]] = Encoder.encodeList[Either[ExtremeCaseValue, ExtremeCaseError]]
  implicit val intEitherEncoder: EntityEncoder[IO, Either[ExtremeCaseValue, ExtremeCaseError]] = jsonEncoderOf[IO, Either[ExtremeCaseValue, ExtremeCaseError]]
  implicit val intListEitherEncoder: EntityEncoder[IO, List[Either[ExtremeCaseValue, ExtremeCaseError]]] = jsonEncoderOf[IO, List[Either[ExtremeCaseValue, ExtremeCaseError]]]

}