package com.innowise
package caseclass

import cats.effect.IO
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder, HCursor}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class RawCaseData(country: String, caseCount: Int, date: String)

object RawCaseData {

  implicit val caseDataApiDecoder: Decoder[RawCaseData] = new Decoder[RawCaseData] {
    final def apply(c: HCursor): Decoder.Result[RawCaseData] =
      for {
        country <- c.downField("Country").as[String]
        caseCount <- c.downField("Cases").as[Int]
        date <- c.downField("Date").as[String]
      } yield {
        RawCaseData(country, caseCount, date)
      }
  }

  implicit val caseDataApiEncoder: Encoder[RawCaseData] = deriveEncoder[RawCaseData]
  implicit val caseDataApiListDecoder: Decoder[List[RawCaseData]] = Decoder.decodeList[RawCaseData]
  implicit val caseDataApiListEncoder: Encoder[List[RawCaseData]] = Encoder.encodeList[RawCaseData]
  implicit val caseDataApiEntityEncoder: EntityEncoder[IO, RawCaseData] = jsonEncoderOf[IO, RawCaseData]
  implicit val caseDataApiEntityDecoder: EntityDecoder[IO, RawCaseData] = jsonOf[IO, RawCaseData]
  implicit val caseDataApiListEntityEncoder: EntityEncoder[IO, List[RawCaseData]] = jsonEncoderOf[IO, List[RawCaseData]]
  implicit val caseDataApiListEntityDecoder: EntityDecoder[IO, List[RawCaseData]] = jsonOf[IO, List[RawCaseData]]


}
