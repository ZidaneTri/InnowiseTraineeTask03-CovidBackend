package com.innowise
package decoder

import cats.effect.IO
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class CountryTimeGap(countryName: String, startDate: String, endDate: String)

object CountryTimeGap {
  
  implicit val countryDataDecoder: Decoder[CountryTimeGap] = deriveDecoder[CountryTimeGap]
  implicit val countryDataEncoder: Encoder[CountryTimeGap] = deriveEncoder[CountryTimeGap]
  implicit val countryDataListDecoder: Decoder[List[CountryTimeGap]] = Decoder.decodeList[CountryTimeGap]
  implicit val countryDataListEncoder: Encoder[List[CountryTimeGap]] = Encoder.encodeList[CountryTimeGap]
  implicit val countryDataEntityEncoder: EntityEncoder[IO, CountryTimeGap] = jsonEncoderOf[IO, CountryTimeGap]
  implicit val countryDataEntityDecoder: EntityDecoder[IO, CountryTimeGap] = jsonOf[IO, CountryTimeGap]
  implicit val countryDataListEntityEncoder: EntityEncoder[IO, List[CountryTimeGap]] = jsonEncoderOf[IO, List[CountryTimeGap]]
  implicit val countryDataListEntityDecoder: EntityDecoder[IO, List[CountryTimeGap]] = jsonOf[IO, List[CountryTimeGap]]
}