/*
 * Copyright (c) 2021 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.pctest.helpers

import java.util.Locale

import com.github.tototoshi.csv.CSVReader
import com.wegtam.pctest.models
import com.wegtam.pctest.models.{ PCResponse, StatisticData }

object EvaluationHelpers {

  /**
    * Fetch the data (CSV files) from the given URLs and return a list
    * with the single rows.
    *
    * @param urls List of URLs to fetch from.
    * @return List of Maps that contain the rows in the form (head -> value)
    */
  def fetchData(urls: List[String]): List[Map[String, String]] =
    urls.foldLeft(List.empty[Map[String, String]]) { (m, url) =>
      try {
        val csvReader = downloadCSV(url)
        val values    = csvReader.allWithHeaders()
        // Remove empty spaces before or after the `header` specification
        val cleanedValues = values.map { e =>
          e.map(old => (old._1.trim, old._2))
        }
        m ++ cleanedValues
      } catch {
        case _: Throwable =>
          m
      }
    }

  /**
    * Download the file from the given URL and return a CSVReader.
    *
    * @param fileUrl The URL of the CSV file
    * @return CSVReader
    */
  def downloadCSV(fileUrl: String): CSVReader = CSVReader.open(scala.io.Source.fromURL(fileUrl))

  /**
    * Analyze the CSV data and return the response for the request
    *
    * @param data Loaded CSV data in the form [(header1 -> val, header2 -> val, ...), ...]
    * @return PCResponse
    */
  def analyzeData(data: List[Map[String, String]]): PCResponse = {
    val intermediate: Map[String, StatisticData] = data.foldLeft(Map.empty[String, StatisticData]) { (s, e) =>
      val redner: Option[String] = e.get("Redner")
      val thema: Option[String]  = e.get("Thema")
      val wordsS: Option[String] = e.get("Wörter")

      (redner, thema, wordsS) match {
        case (Some(r), Some(t), Some(w)) =>
          val securityMentioned: Boolean = t.trim.toLowerCase(Locale.ROOT).equals("innere sicherheit")
          val iS: Int                    = if (securityMentioned) 1 else 0
          val wordsI: Int =
            try w.trim.toInt
            catch {
              case _: Throwable =>
                println(s"Words could not be casted for speaker ${redner.getOrElse("")}")
                0
            }
          val name = r.trim
          // Update existing entry if the `Redner` already exists
          if (s.contains(name)) {
            val entryO: Option[StatisticData] = s.get(name)
            entryO.fold(s) { entry =>
              val newEntry: StatisticData = entry.copy(
                appearances = entry.appearances + 1,
                wordsTotal = entry.wordsTotal + wordsI,
                securityMentioned = entry.securityMentioned + iS
              )
              s + (name -> newEntry)
            }
          } else
            s + (name -> StatisticData(1, wordsI, iS))
        case _ => s
      }
    }
    // Sort the results depending on the requirements
    val mostSecurityMentioned = intermediate.toSeq.sortWith(_._2.securityMentioned > _._2.securityMentioned).take(2)
    val fewestWords           = intermediate.toSeq.sortWith(_._2.wordsTotal < _._2.wordsTotal).take(2)
    val mostAppearances       = intermediate.toSeq.sortWith(_._2.appearances > _._2.appearances).take(2)

    // If there is no unique answer, we return `Null`
    val mSMResponse: Option[String] = checkResponseSecurityMentioned(mostSecurityMentioned)
    val mAResponse: Option[String]  = checkResponseMostAppearances(mostAppearances)
    val lWResponse: Option[String]  = checkResponseFewestWords(fewestWords)

    PCResponse(
      mostSpeeches = mAResponse,
      mostSecurity = mSMResponse,
      leastWordy = lWResponse
    )

  }

  /**
    * Check if there is a unique answer for this question which
    * speaker mentioned security most.
    * If not, we return Null.
    *
    * @param members Seq
    *                List of the top most of often mentioned speakers.
    * @return Option[String]
    *         Name of the speaker or Null.
    */
  def checkResponseSecurityMentioned(members: Seq[(String, models.StatisticData)]): Option[String] =
    if (members.length > 1) {
      val first  = members.headOption
      val second = members.drop(1).headOption
      (first, second) match {
        case (Some(f), Some(s)) =>
          if (f._2.securityMentioned == s._2.securityMentioned) Option.empty[String] else Some(f._1)
        case _ => Option.empty[String]
      }
    } else {
      members.headOption.fold[Option[String]](None)(e => Some(e._1))
    }

  /**
    * Check if there is a unique answer for this question which speaker
    * appeared most.
    * If not, we return Null.
    *
    * @param members Seq
    *                List of the top most of often mentioned speakers.
    * @return Option[String]
    *         Name of the speaker or Null.
    */
  def checkResponseMostAppearances(members: Seq[(String, models.StatisticData)]): Option[String] =
    if (members.length > 1) {
      val first  = members.headOption
      val second = members.drop(1).headOption
      (first, second) match {
        case (Some(f), Some(s)) =>
          if (f._2.appearances == s._2.appearances) Option.empty[String] else Some(f._1)
        case _ => Option.empty[String]
      }
    } else {
      members.headOption.fold[Option[String]](None)(e => Some(e._1))
    }

  /**
    * Check if there is a unique answer for this question which speaker
    * used the fewest words.
    * If not, we return Null.
    *
    * @param members Seq
    *                List of the top most of the best candidates.
    * @return Option[String]
    *         Name of the speaker or Null.
    */
  def checkResponseFewestWords(members: Seq[(String, models.StatisticData)]): Option[String] =
    if (members.length > 1) {
      val first  = members.headOption
      val second = members.drop(1).headOption
      (first, second) match {
        case (Some(f), Some(s)) =>
          if (f._2.wordsTotal == s._2.wordsTotal) Option.empty[String] else Some(f._1)
        case _ => Option.empty[String]
      }
    } else {
      members.headOption.fold[Option[String]](None)(e => Some(e._1))
    }

}
