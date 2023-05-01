package com.innowise
package decoder

import cats.effect.IO
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

case class CountryData(country: String, slug: String)

object CountryData {
  
  implicit val countryDataDecoder: Decoder[CountryData] = new Decoder[CountryData] {
    final def apply(c: HCursor): Decoder.Result[CountryData] =
      for {
        country <- c.downField("Country").as[String]
        slug <- c.downField("Slug").as[String]
      } yield {
        CountryData(country, slug)
      }
  }
  
  implicit val countryDataEncoder: Encoder[CountryData] = deriveEncoder[CountryData]
  implicit val countryDataListDecoder: Decoder[List[CountryData]] = Decoder.decodeList[CountryData]
  implicit val countryDataListEncoder: Encoder[List[CountryData]] = Encoder.encodeList[CountryData]
  implicit val countryDataEntityEncoder: EntityEncoder[IO, CountryData] = jsonEncoderOf[IO, CountryData]
  implicit val countryDataEntityDecoder: EntityDecoder[IO, CountryData] = jsonOf[IO, CountryData]
  implicit val countryDataListEntityEncoder: EntityEncoder[IO, List[CountryData]] = jsonEncoderOf[IO, List[CountryData]]
  implicit val countryDataListEntityDecoder: EntityDecoder[IO, List[CountryData]] = jsonOf[IO, List[CountryData]]

}
