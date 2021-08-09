/*
 * Copyright (c) 2021 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.pctest.models

import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }

/**
  * The response of the CSV request which provides the statistical results.
  *
  * @param mostSpeeches Option[String]
  *                     Which speaker made the most speeches?
  * @param mostSecurity Option[String]
  *                     This speker mentioned `Innere Sicherheit` most?
  * @param leastWordy   Option[String]
  *                     Which speaker used the fewest number of words?
  */
final case class PCResponse(
    mostSpeeches: Option[String],
    mostSecurity: Option[String],
    leastWordy: Option[String]
)

object PCResponse {

  implicit val PCResponseEncoder: Encoder[PCResponse] = deriveEncoder[PCResponse]
  implicit val PCResponseDecoder: Decoder[PCResponse] = deriveDecoder[PCResponse]

}
