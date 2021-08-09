/*
 * Copyright (c) 2021 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.pctest.api

import cats.effect._
import cats.implicits._
import com.wegtam.pctest.helpers.EvaluationHelpers
import com.wegtam.pctest.models._
import org.http4s.HttpRoutes
import org.http4s.dsl._
import sttp.model.{ QueryParams, StatusCode }
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

final class Evaluation[F[_]: Concurrent: ContextShift: Timer]() extends Http4sDsl[F] {

  private val evaluation: HttpRoutes[F] = Http4sServerInterpreter.toRoutes(Evaluation.evaluation) { queryParams =>
    if (queryParams.toSeq.isEmpty) {
      Sync[F].delay(Left(StatusCode.BadRequest))
    } else
      for {
        urls     <- Sync[F].pure(queryParams.toSeq.map(_._2).toList)
        data     <- Sync[F].delay(EvaluationHelpers.fetchData(urls))
        analyzed <- Sync[F].delay(EvaluationHelpers.analyzeData(data).asRight[StatusCode])
      } yield analyzed
  }

  val routes: HttpRoutes[F] = evaluation

}

object Evaluation {

  val evaluation: Endpoint[QueryParams, StatusCode, PCResponse, Any] =
    endpoint.get
      .in("evaluation")
      .in(queryParams)
      .errorOut(statusCode)
      .out(jsonBody[PCResponse])

}
