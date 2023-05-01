package com.innowise
package service

import decoder.{CaseDataApi, CountryData, CountryError, CountryTimeGap, DayCaseCount, ExtremeCaseValue}

import cats.effect.IO
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import cats.effect.*
import cats.syntax.all.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.Uri

import java.time.{Instant, LocalDateTime, ZoneOffset}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CountryService(client: Client[IO]) {

  var dataCountry: mutable.Map[String, String] = mutable.Map.empty

  val covidApiUri = uri"https://api.covid19api.com"


  val countriesUri = covidApiUri / "/countries"

   def getCountries: IO[List[CountryData]] = {
     for {
       covidCasesList <- client.expect[List[CountryData]](countriesUri)
       covidCasesListClone = saveToMap(covidCasesList)
     } yield covidCasesList
   }

  //the dirty hack to have all country data in map. Probably the greatest mistake here.
   def saveToMap(countryDataList: List[CountryData]): List[CountryData] = {
     if(dataCountry.isEmpty){
       for (singleCountryData <- countryDataList) {
         dataCountry.addOne(singleCountryData.country, singleCountryData.slug)
       }
     }
     countryDataList
   }

  def getExtremeCases (timeGaps: List[CountryTimeGap]): IO[Int] = {
    val fixedTimeGaps: List[CountryTimeGap] = timeGaps.map(addOverheadToDate)
    for {
      caselist <- getCovidCasesFromApi(fixedTimeGaps.head)
      dayCase = getDayCaseCount(caselist)
      sum = summarise(dayCase)
    } yield sum
  }

  def addOverheadToDate(timeGap: CountryTimeGap): CountryTimeGap = {
    val startDate = LocalDateTime.ofInstant(Instant.parse(timeGap.startDate), ZoneOffset.UTC).minusDays(1)
    // endDate = LocalDateTime.ofInstant(Instant.parse(timeGap.endDate), ZoneOffset.UTC).plusDays(1)
    CountryTimeGap(timeGap.countryName,startDate.toString, timeGap.endDate)
  }

  def getCovidCasesFromApi(countryTimeGap: CountryTimeGap): IO[List[CaseDataApi]] = {

    val uri = covidApiUri / "country" / countryTimeGap.countryName / "status" / "confirmed" +?
      ("from", countryTimeGap.startDate) +? ("to", countryTimeGap.endDate)
    for {
      covidCasesList <- client.expect[List[CaseDataApi]](uri)
    } yield covidCasesList
  }

  def getDayCaseCount(apiList: List[CaseDataApi]): List[DayCaseCount] = {
    val buffer: ListBuffer[DayCaseCount] = ListBuffer.empty
    for i <- 1 until apiList.length do {
      buffer.addOne(DayCaseCount(apiList(i).date, apiList(i).caseCount-apiList(i-1).caseCount))
    }
    buffer.toList
  }

  /*def getExtremeCasesValue(rawData: List[DayCaseCount]): ExtremeCaseValue = {

  } */

  def summarise(list: List[DayCaseCount]): Int = {
    val sum:Int = list.map(_.caseCount).sum
    sum
  }


}


