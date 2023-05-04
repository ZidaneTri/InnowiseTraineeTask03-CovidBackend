package com.innowise
package route

import caseclass.{CountryInfo, ExtremeCaseError, ExtremeCaseValue, TimeGap}
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

  def countriesRoute(countryService: CountryService): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "countries" => {
        Ok(countryService.getCountries)
      }
    }

  def extremeCasesRoute(countryService: CountryService): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "extreme" =>
        for {
          timeGapList <- req.as[List[TimeGap]]
          caseList = countryService.getExtremeCases(timeGapList)
          resp <- Ok(caseList)
        } yield (resp)
      }

}
