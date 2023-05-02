package com.innowise
package route

import decoder.{CountryInfo, ExtremeCaseError, ExtremeCaseValue, TimeGap}
import service.CountryService

import cats.effect.IO
import io.circe.{Decoder, Encoder, Json, JsonObject}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.io.*
import io.circe.Encoder.AsArray.importedAsArrayEncoder
import io.circe.Encoder.AsObject.importedAsObjectEncoder
import io.circe.Encoder.AsRoot.importedAsRootEncoder
import cats.syntax.all.*
import io.circe.Encoder.AsObject

object CovidRoutes {

 // implicit def decodeEither[ExtremeCaseValue, ExtremeCaseError](implicit decoderA: Decoder[ExtremeCaseValue], decoderB: Decoder[ExtremeCaseError]): Decoder[Either[ExtremeCaseValue, ExtremeCaseError]] = decoderA.either(decoderB)
  //implicit def encodeEither[ExtremeCaseValue, ExtremeCaseError](implicit encoderA: Encoder[ExtremeCaseValue], encoderB: Encoder[ExtremeCaseError]): Encoder[Either[ExtremeCaseValue, ExtremeCaseError]] = encoderA.either(encoderB)
  implicit val eitherEncoder: Encoder[Either[ExtremeCaseValue, ExtremeCaseError]] = deriveEncoder[Either[ExtremeCaseValue, ExtremeCaseError]]
 /* implicit def encodeEither[ExtremeCaseValue, ExtremeCaseError](implicit encodeA: Encoder[ExtremeCaseValue], encodeB: Encoder[ExtremeCaseError]
  ): AsObject[Either[ExtremeCaseValue, ExtremeCaseError]] = {
    case Left(a) =>
    case Right(b) => encodeB(b)
  }*/
  implicit val eitherListEncoder: Encoder[List[Either[ExtremeCaseValue, ExtremeCaseError]]] = Encoder.encodeList[Either[ExtremeCaseValue, ExtremeCaseError]]
  implicit val intEitherEncoder: EntityEncoder[IO, Either[ExtremeCaseValue, ExtremeCaseError]] = jsonEncoderOf[IO, Either[ExtremeCaseValue, ExtremeCaseError]]
  implicit val intListEitherEncoder: EntityEncoder[IO, List[Either[ExtremeCaseValue, ExtremeCaseError]]] = jsonEncoderOf[IO, List[Either[ExtremeCaseValue, ExtremeCaseError]]]

  def helloWorldRoutes(countryService: CountryService): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" => {
        Ok(countryService.getCountries)
      }
    }

  def testRoutes(countryService: CountryService): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "test" =>
        for {
          timeGapList <- req.as[List[TimeGap]]
          caseList = countryService.getExtremeCases(timeGapList)
          resp <- Ok(caseList)
        } yield (resp)
      }

}
