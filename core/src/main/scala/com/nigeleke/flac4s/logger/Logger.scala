/*
 * Copyright (c) 2023, Nigel Eke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.nigeleke.flac4s
package logger

import scodec.*
import scodec.codecs.*

import com.typesafe.config.*

object Logger:

  lazy val config             = ConfigFactory.load()
  lazy val configuredlogLevel = LogLevel.valueOf(config.getString("flac4s.logging.logLevel"))

  enum LogLevel:
    case None, Metadata, Field, Audio

  private def log[A](name: String, logLevel: LogLevel)(codec: Codec[A]) =
    val logThis = configuredlogLevel.ordinal >= logLevel.ordinal
    if logThis
    then logToStdOut((name | codec), name)
    else (name | codec)

  def logField[A](name: String)(codec: Codec[A])    = log(name, LogLevel.Field)(codec)
  def logMetadata[A](name: String)(codec: Codec[A]) = log(name, LogLevel.Metadata)(codec)
  def logAudio[A](name: String)(codec: Codec[A])    = log(name, LogLevel.Audio)(codec)
