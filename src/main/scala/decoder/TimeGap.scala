package com.innowise
package decoder

import cats.effect.IO
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class TimeGap(countryName: String, startDate: String, endDate: String)

object TimeGap {
  
  implicit val countryDataDecoder: Decoder[TimeGap] = deriveDecoder[TimeGap]
  implicit val countryDataEncoder: Encoder[TimeGap] = deriveEncoder[TimeGap]
  implicit val countryDataListDecoder: Decoder[List[TimeGap]] = Decoder.decodeList[TimeGap]
  implicit val countryDataListEncoder: Encoder[List[TimeGap]] = Encoder.encodeList[TimeGap]
  implicit val countryDataEntityEncoder: EntityEncoder[IO, TimeGap] = jsonEncoderOf[IO, TimeGap]
  implicit val countryDataEntityDecoder: EntityDecoder[IO, TimeGap] = jsonOf[IO, TimeGap]
  implicit val countryDataListEntityEncoder: EntityEncoder[IO, List[TimeGap]] = jsonEncoderOf[IO, List[TimeGap]]
  implicit val countryDataListEntityDecoder: EntityDecoder[IO, List[TimeGap]] = jsonOf[IO, List[TimeGap]]
}