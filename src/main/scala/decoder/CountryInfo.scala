package com.innowise
package decoder

import cats.effect.IO
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

case class CountryInfo(country: String, slug: String)

object CountryInfo {
  
  implicit val countryDataDecoder: Decoder[CountryInfo] = new Decoder[CountryInfo] {
    final def apply(c: HCursor): Decoder.Result[CountryInfo] =
      for {
        country <- c.downField("Country").as[String]
        slug <- c.downField("Slug").as[String]
      } yield {
        CountryInfo(country, slug)
      }
  }
  
  implicit val countryDataEncoder: Encoder[CountryInfo] = deriveEncoder[CountryInfo]
  implicit val countryDataListDecoder: Decoder[List[CountryInfo]] = Decoder.decodeList[CountryInfo]
  implicit val countryDataListEncoder: Encoder[List[CountryInfo]] = Encoder.encodeList[CountryInfo]
  implicit val countryDataEntityEncoder: EntityEncoder[IO, CountryInfo] = jsonEncoderOf[IO, CountryInfo]
  implicit val countryDataEntityDecoder: EntityDecoder[IO, CountryInfo] = jsonOf[IO, CountryInfo]
  implicit val countryDataListEntityEncoder: EntityEncoder[IO, List[CountryInfo]] = jsonEncoderOf[IO, List[CountryInfo]]
  implicit val countryDataListEntityDecoder: EntityDecoder[IO, List[CountryInfo]] = jsonOf[IO, List[CountryInfo]]

}
