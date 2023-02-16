package com.nigeleke.flac
package codecs
package frame

import scodec.*
import scodec.codecs.*

case class FrameFooter(crc: Int)

object FrameFooter:

  //  <16>	CRC-16 (polynomial = x^16 + x^15 + x^2 + x^0, initialized with 0) of everything before the crc, back to and including the frame header sync code
  //
  private val crcCodec = log("crc")(uint16)

  val codec: Codec[FrameFooter] = log("frameFooter") { crcCodec }.as[FrameFooter]
