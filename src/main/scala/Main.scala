package com.innowise

import cats.effect.*
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, Uri}
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import cats.effect.{IO, IOApp}
import com.innowise.caseclass.CountryInfo
import com.innowise.route.CovidRoutes
import com.innowise.service.CountryService
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import io.circe.*
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.literal.*
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.headers.Origin
import org.http4s.server.middleware.CORS

object Main extends IOApp.Simple {

  val run: IO[Unit] = {
    for {
      client <- EmberClientBuilder.default[IO].build
      countryService = CountryService(client)
      httpApp = {
        CovidRoutes.countriesRoute(countryService)<+>
          CovidRoutes.extremeCasesRoute(countryService)
      }.orNotFound

      corsHttpApp = CORS.policy
        .withAllowOriginHost(Set(
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(4200))
        ))
        .withAllowCredentials(false)
        .httpApp(httpApp)

      _ <- EmberServerBuilder.default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(corsHttpApp)
      .build
    } yield()
  }.useForever




}
