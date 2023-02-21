package com.nigeleke.flac
package logger

import scodec.*
import scodec.codecs.*

object Logger:

  enum LogLevel:
    case None, MetadataHeader, MetadataField, Audio

  def log[A](name: String, logLevel: LogLevel)(codec: Codec[A])(given config: Config) =
    val logThis = config.logLevel.ordinal >= logLevel.ordinal
    if logThis
    then logToStdOut((name | codec), name)
    else (name | codec)