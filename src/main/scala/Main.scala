package com.innowise

import cats.effect.*
import cats.syntax.all._
import com.comcast.ip4s.*
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import cats.effect.{IO, IOApp}
import com.innowise.decoder.CountryData
import com.innowise.route.CovidRoutes
import com.innowise.service.CountryService
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import io.circe.*
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.literal.*
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object Main extends IOApp.Simple {

  val run: IO[Unit] = {
    for {
      client <- EmberClientBuilder.default[IO].build
      countryService = CountryService(client)
      httpApp = {
        CovidRoutes.helloWorldRoutes(countryService)<+>
          CovidRoutes.testRoutes(countryService)
      }.orNotFound

      _ <- EmberServerBuilder.default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
    } yield()
  }.useForever




}
