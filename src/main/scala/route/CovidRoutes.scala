package com.innowise
package route

import decoder.{CountryTimeGap, ExtremeCaseValue}
import service.CountryService

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.io.*
import io.circe.Encoder.AsArray.importedAsArrayEncoder
import io.circe.Encoder.AsObject.importedAsObjectEncoder
import io.circe.Encoder.AsRoot.importedAsRootEncoder

object CovidRoutes {

  implicit val intEntityEncoder: EntityEncoder[IO, Int] = jsonEncoderOf[IO, Int]
  implicit val testEncoder: EntityEncoder[IO, List[IO[Int]]] = jsonEncoderOf[IO, List[IO[Int]]]

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
          timeGapList <- req.as[List[CountryTimeGap]]
          caseList = countryService.getExtremeCases(timeGapList)
          resp <- Ok(caseList)
        } yield (resp)
      }

}
