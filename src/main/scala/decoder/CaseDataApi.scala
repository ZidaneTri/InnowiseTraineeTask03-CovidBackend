package com.innowise
package decoder

import cats.effect.IO
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder, HCursor}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class CaseDataApi(country: String, caseCount: Int, date: String)

object CaseDataApi {

  implicit val caseDataApiDecoder: Decoder[CaseDataApi] = new Decoder[CaseDataApi] {
    final def apply(c: HCursor): Decoder.Result[CaseDataApi] =
      for {
        country <- c.downField("Country").as[String]
        caseCount <- c.downField("Cases").as[Int]
        date <- c.downField("Date").as[String]
      } yield {
        CaseDataApi(country, caseCount, date)
      }
  }

  implicit val caseDataApiEncoder: Encoder[CaseDataApi] = deriveEncoder[CaseDataApi]
  implicit val caseDataApiListDecoder: Decoder[List[CaseDataApi]] = Decoder.decodeList[CaseDataApi]
  implicit val caseDataApiListEncoder: Encoder[List[CaseDataApi]] = Encoder.encodeList[CaseDataApi]
  implicit val caseDataApiEntityEncoder: EntityEncoder[IO, CaseDataApi] = jsonEncoderOf[IO, CaseDataApi]
  implicit val caseDataApiEntityDecoder: EntityDecoder[IO, CaseDataApi] = jsonOf[IO, CaseDataApi]
  implicit val caseDataApiListEntityEncoder: EntityEncoder[IO, List[CaseDataApi]] = jsonEncoderOf[IO, List[CaseDataApi]]
  implicit val caseDataApiListEntityDecoder: EntityDecoder[IO, List[CaseDataApi]] = jsonOf[IO, List[CaseDataApi]]


}
