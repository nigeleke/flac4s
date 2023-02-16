package com.nigeleke.flac
package codecs
package frame

import metadata.StreamInfo

import scodec.*
import scodec.codecs.*

case class SubframeConstant(constant: Long) extends SubframeContent

object SubframeConstant:

  //  <n>	Unencoded constant value of the subblock, n = frame's bits-per-sample.
  //
  def codec(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ): Codec[SubframeConstant] = {
    val bitsCount = frameHeader.bitDepth - subframeHeader.numberOfWastedBits
    ulong(bitsCount)
  }.as[SubframeConstant]
