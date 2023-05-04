package com.innowise

import route.CovidRoutes
import service.CountryService

import cats.effect.{IO, Resource}
import com.innowise.caseclass.{ExtremeCaseError, ExtremeCaseValue, TimeGap}
import org.http4s.{EntityDecoder, HttpRoutes, Method, Request, Response, Status, UrlForm}
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.uri
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import cats.syntax.all.*
import io.circe.*
import io.circe.syntax.*
import io.circe.literal.*
import org.http4s.circe.*
import cats.effect.unsafe.IORuntime

import scala.collection.mutable
import scala.language.postfixOps
import org.scalactic.Prettifier.default


class ExtremeCasesTest extends AnyFunSuite with BeforeAndAfterAll {

  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  implicit def eitherEncoder[ExtremeCaseValue, ExtremeCaseError](implicit encodeA: Encoder[ExtremeCaseValue], encodeB: Encoder[ExtremeCaseError]
                                                                ): Encoder[Either[ExtremeCaseValue, ExtremeCaseError]] = {
    case Left(a) => encodeA(a)
    case Right(b) => encodeB(b)
  }

  implicit val eitherListEncoder: Encoder[List[Either[ExtremeCaseValue, ExtremeCaseError]]] = Encoder.encodeList[Either[ExtremeCaseValue, ExtremeCaseError]]

  val client: Client[IO] = JavaNetClientBuilder[IO].create
  val countryService: CountryService = CountryService(client)
  val extremeCasesRoute: HttpRoutes[IO] = CovidRoutes.extremeCasesRoute(countryService)



  override def beforeAll() = {
    countryService.dataCountry.addAll(List("Belarus" -> "belarus", "China" -> "china"))
  }

  test ("Single country, should be successful and return data") {
    val expected = List(ExtremeCaseValue("Belarus",
      561, List("2021-02-09T00:00:00Z"),
      1799, List("2021-02-10T00:00:00Z"))).asJson
    val requestBody = List(TimeGap("Belarus", "2021-02-02T00:00:00.000Z","2021-02-10T00:00:00.000Z")).asJson

    assert(check[Json](extremeCaseResponse(requestBody), Status.Ok, Some(expected)))
  }

  test("Single wrong country, should be successful and return error data") {
    val expected = List(ExtremeCaseError("Belarus2","The country you entered is wrong")).asJson
    val requestBody = List(TimeGap("Belarus2", "2021-02-02T00:00:00.000Z","2021-02-10T00:00:00.000Z")).asJson

    assert(check[Json](extremeCaseResponse(requestBody), Status.Ok, Some(expected)))
  }

  test("Single country with wrong dates, should be successful and return error data") {
    val expected = List(ExtremeCaseError("Belarus", "The dates you entered is wrong")).asJson

    val requestBodyWrongStart = List(TimeGap("Belarus", "2019-02-02T00:00:00.000Z","2021-02-10T00:00:00.000Z")).asJson
    val requestBodyWrongEnd = List(TimeGap("Belarus", "2021-02-02T00:00:00.000Z","2024-02-10T00:00:00.000Z")).asJson
    val requestBodyWrongEndEarlier = List(TimeGap("Belarus", "2022-02-02T00:00:00.000Z","2021-02-10T00:00:00.000Z")).asJson

    assert(check[Json](extremeCaseResponse(requestBodyWrongStart), Status.Ok, Some(expected)))
    assert(check[Json](extremeCaseResponse(requestBodyWrongEnd), Status.Ok, Some(expected)))
    assert(check[Json](extremeCaseResponse(requestBodyWrongEndEarlier), Status.Ok, Some(expected)))
  }

  test("Two countries, should be successful and return data") {
    val expected = List(
      ExtremeCaseValue("Belarus",
        561, List("2021-02-09T00:00:00Z"),
        1799, List("2021-02-10T00:00:00Z")),
      ExtremeCaseValue("China",
        14, List("2021-05-27T00:00:00Z"),
        80, List("2021-06-07T00:00:00Z"))
    ).asJson

    val requestBody = List(
      TimeGap("Belarus", "2021-02-02T00:00:00.000Z","2021-02-10T00:00:00.000Z"),
      TimeGap("China", "2021-05-10T00:00:00.000Z", "2021-06-12T00:00:00.000Z")).asJson

    assert(check[Json](extremeCaseResponse(requestBody), Status.Ok, Some(expected)))
  }

  test("Two countries, should be successful, one returns value, other returns error") {
    val expected = List[Either[ExtremeCaseValue, ExtremeCaseError]](
      Left(ExtremeCaseValue("Belarus",
        561, List("2021-02-09T00:00:00Z"),
        1799, List("2021-02-10T00:00:00Z"))),
      Right(ExtremeCaseError("China","The dates you entered is wrong"))
    ).asJson

    val requestBody = List(
      TimeGap("Belarus", "2021-02-02T00:00:00.000Z", "2021-02-10T00:00:00.000Z"),
      TimeGap("China", "2024-05-10T00:00:00.000Z", "2021-06-12T00:00:00.000Z")).asJson

    assert(check[Json](extremeCaseResponse(requestBody), Status.Ok, Some(expected)))
  }

  def check[A](actual: IO[Response[IO]],
               expectedStatus: Status,
               expectedBody: Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Boolean = {
    val actualResp = actual.unsafeRunSync()
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck = expectedBody.fold[Boolean](
      // Verify Response's body is empty.
      actualResp.body.compile.toVector.unsafeRunSync().isEmpty)(
      expected => actualResp.as[A].unsafeRunSync() == expected
    )
    statusCheck && bodyCheck
  }

  def extremeCaseResponse(body: Json): IO[Response[IO]] = {
    val request: Request[IO] = Request(method = Method.POST, uri = uri"/extreme")
      .withEntity(body)
    extremeCasesRoute.orNotFound.run(request)
  }





}
