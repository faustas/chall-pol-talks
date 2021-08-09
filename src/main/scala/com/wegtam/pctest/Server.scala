/*
 * Copyright (c) 2021 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.pctest

import cats.effect._
import com.typesafe.config._
import com.wegtam.pctest.api._
import com.wegtam.pctest.config._
import eu.timepit.refined.auto._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._

import scala.concurrent.ExecutionContext

object Server extends IOApp.WithContext {
  val ec: ExecutionContext = ExecutionContext.global

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] = Resource.eval(SyncIO(ec))

  override def run(args: List[String]): IO[ExitCode] = {
    val program = for {
      config <- IO(ConfigFactory.load(getClass().getClassLoader()))
      serviceConfig <- IO(
        ConfigSource.fromConfig(config).at(ServiceConfig.CONFIG_KEY).loadOrThrow[ServiceConfig]
      )
      evaluationRoutes = new Evaluation[IO]()
      routes           = evaluationRoutes.routes
      httpApp          = Router("/" -> routes).orNotFound
      server = BlazeServerBuilder[IO](ec)
        .bindHttp(serviceConfig.port, serviceConfig.ip)
        .withHttpApp(httpApp)
      fiber = server.serve.compile.drain.as(ExitCode.Success)
    } yield fiber
    program.attempt.unsafeRunSync() match {
      case Left(e) =>
        IO {
          e.printStackTrace()
          ExitCode.Error
        }
      case Right(s) => s
    }
  }

}
