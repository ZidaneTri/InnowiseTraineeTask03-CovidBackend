package com.innowise
package service

import decoder.{CountryInfo, DayCaseCount, ExtremeCase, ExtremeCaseError, ExtremeCaseValue, RawCaseData, TimeGap}
import cats.effect.*
import cats.syntax.all.*
import org.http4s.*
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.implicits.*

import java.time.{Instant, LocalDateTime, ZoneOffset}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CountryService(client: Client[IO]) {

  var dataCountry: mutable.Map[String, String] = mutable.Map.empty

  val covidApiUri = uri"https://api.covid19api.com"

  val countriesUri = covidApiUri / "/countries"

  val minDate = LocalDateTime.of(2020, 1, 23, 0,0)


   def getCountries: IO[List[CountryInfo]] = {
     for {
       covidCasesList <- client.expect[List[CountryInfo]](countriesUri)
       covidCasesListClone = saveToMap(covidCasesList)
     } yield covidCasesList
   }

  //the dirty hack to have all country data in map. Probably the greatest mistake here.
   def saveToMap(countryDataList: List[CountryInfo]): List[CountryInfo] = {
     if(dataCountry.isEmpty){
       for (singleCountryData <- countryDataList) {
         dataCountry.addOne(singleCountryData.country, singleCountryData.slug)
       }
     }
     countryDataList
   }

  def getExtremeCases (timeGaps: List[TimeGap]): IO[List[ExtremeCaseValue]] = {
    val fixedTimeGaps: List[TimeGap] = timeGaps.map(convertAndOverheadToDate)
    val result: IO[List[ExtremeCaseValue]] = fixedTimeGaps.traverse(getCovidCasesFromApi)
    result
  }

  def convertAndOverheadToDate(timeGap: TimeGap): TimeGap = {
    val startDate = LocalDateTime.ofInstant(Instant.parse(timeGap.startDate), ZoneOffset.UTC).minusDays(1)
    val endDate = LocalDateTime.ofInstant(Instant.parse(timeGap.endDate), ZoneOffset.UTC)
    TimeGap(timeGap.countryName,startDate.toString, timeGap.endDate)
  }

  def getCovidCasesFromApi(timeGap: TimeGap): IO[ExtremeCaseValue] = {

    val uri = covidApiUri / "total" / "country" / timeGap.countryName / "status" / "confirmed" +?
      ("from", timeGap.startDate) +? ("to", timeGap.endDate)

    /*if(validateDates(timeGap)){
      IO(ExtremeCaseError(timeGap.countryName, "The dates you entered is wrong"))
    }*/
    for {
      covidCasesList <- client.expect[List[RawCaseData]](uri)
      dayCase = getDayCaseCount(covidCasesList)
      extremeCase = getExtremeCaseValue(timeGap.countryName, dayCase)
    } yield extremeCase
      //).orElse(IO(ExtremeCaseError(timeGap.countryName, "Either external API is unreachable or country name you entered is wrong")))
  }

  def getDayCaseCount(apiList: List[RawCaseData]): List[DayCaseCount] = {
    val buffer: ListBuffer[DayCaseCount] = ListBuffer.empty
    for i <- 1 until apiList.length do {
      buffer.addOne(DayCaseCount(apiList(i).date, apiList(i).caseCount-apiList(i-1).caseCount))
    }
    buffer.toList
  }

  def getExtremeCaseValue(country: String, rawData: List[DayCaseCount]): ExtremeCaseValue = {
    val ascendingRawData = rawData.sortWith(_.caseCount < _.caseCount)
    val descendingRawData = rawData.sortWith(_.caseCount > _.caseCount)
    val minCaseDatesBuffer: ListBuffer[String] = ListBuffer.empty
    val maxCaseDatesBuffer: ListBuffer[String] = ListBuffer.empty
    val minCaseCount = ascendingRawData.head.caseCount
    val maxCaseCount = descendingRawData.head.caseCount
    for i <- 0 until ascendingRawData.length do {
      if(ascendingRawData(i).caseCount == minCaseCount)
        minCaseDatesBuffer.addOne(ascendingRawData(i).date)
    }
    for i <- 0 until descendingRawData.length do {
      if(descendingRawData(i).caseCount == maxCaseCount)
        maxCaseDatesBuffer.addOne(descendingRawData(i).date)
    }
    ExtremeCaseValue(country, minCaseCount, minCaseDatesBuffer.toList, maxCaseCount, maxCaseDatesBuffer.toList)

  }

  def validateDates(timeGap: TimeGap): Boolean = {
    val startDate = LocalDateTime.parse(timeGap.startDate)
    val endDate = LocalDateTime.parse(timeGap.endDate)
    if(startDate.compareTo(endDate) > 0)
      true
    else if(startDate.compareTo(minDate) < 0)
      true
    else if(endDate.compareTo(LocalDateTime.now()) > 0)
      true
    else false
  }



}


