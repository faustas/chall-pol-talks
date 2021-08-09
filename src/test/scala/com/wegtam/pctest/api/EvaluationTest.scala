/*
 * Copyright (c) 2021 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.pctest.api

import cats.effect._
import com.wegtam.pctest.models.PCResponse
import io.circe.Json
import io.circe.syntax._
import munit._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router

class EvaluationTest extends CatsEffectSuite {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  test("GET without provided urls must return 400 - OK") {
    val expectedStatusCode = Status.BadRequest

    def service: HttpRoutes[IO] = Router("/" -> new Evaluation[IO]().routes)

    val req      = Request[IO](method = Method.GET, uri = Uri(path = "/evaluation"))
    val response = service.orNotFound.run(req)
    response.map { r =>
      assertEquals(r.status, expectedStatusCode)
    }
  }

  test("GET with provided urls must return 200 - OK") {
    val expectedStatusCode = Status.Ok

    def service: HttpRoutes[IO] = Router("/" -> new Evaluation[IO]().routes)

    val req = Request[IO](
      method = Method.GET,
      uri = Uri(path = "/evaluation", query = Query(("url1", Some("https://www.example.com"))))
    )
    val response = service.orNotFound.run(req)
    response.map { r =>
      for {
        body <- r.as[Json].unsafeToFuture()
      } yield {
        assertEquals(body, PCResponse(None, None, None).asJson)
        assertEquals(r.status, expectedStatusCode)
      }
    }
  }
}
