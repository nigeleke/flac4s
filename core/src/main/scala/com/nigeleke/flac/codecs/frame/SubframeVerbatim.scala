package com.nigeleke.flac
package codecs
package frame

import metadata.StreamInfo

import scodec.*
import scodec.codecs.*

case class SubframeVerbatim(subblock: Vector[Long]) extends SubframeContent

object SubframeVerbatim:

  //  <n*i>	Unencoded subblock; n = frame's bits-per-sample, i = frame's blocksize.
  //
  def subblockCodec(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ) =
    val bitsCount = frameHeader.bitDepth - subframeHeader.numberOfWastedBits
    log("subblock")(vectorOfN(provide(frameHeader.blockSize), ulong(bitsCount)))

  def codec(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ): Codec[SubframeVerbatim] =
    log("subframeVerbatim") { subblockCodec }.as[SubframeVerbatim]
