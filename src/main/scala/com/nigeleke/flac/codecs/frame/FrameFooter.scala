package com.nigeleke.flac.codecs.frame

import scodec._
import scodec.codecs._

case class FrameFooter(crc: Int)

object FrameFooter {

  //  <16>	CRC-16 (polynomial = x^16 + x^15 + x^2 + x^0, initialized with 0) of everything before the crc, back to and including the frame header sync code
  //
  val codec : Codec[FrameFooter] = {
    ("crc" | uint16).hlist
  }.as[FrameFooter]

}
