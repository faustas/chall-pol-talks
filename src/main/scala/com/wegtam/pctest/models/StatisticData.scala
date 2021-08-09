/*
 * Copyright (c) 2021 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.pctest.models

/**
  * Helper class to store the statistical data for each speaker.
  *
  * @param appearances       How many speeches he/she did.
  * @param wordsTotal        How many words said.
  * @param securityMentioned How often spoke about a security topic.
  */
final case class StatisticData(
    appearances: Int,
    wordsTotal: Int,
    securityMentioned: Int
)
