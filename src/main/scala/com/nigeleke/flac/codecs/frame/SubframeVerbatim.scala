package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata.StreamInfo
import scodec.codecs._

case class SubframeVerbatim(subblock: Vector[Long]) extends SubframeContent

object SubframeVerbatim {

  //  <n*i>	Unencoded subblock; n = frame's bits-per-sample, i = frame's blocksize.
  //
  def codec(implicit frameHeader: FrameHeader, streamInfo: StreamInfo) = {
    println(s"SubframeVerbatim:: framesBitsPerSample: $framesBitsPerSample  framesBlockSize: $framesBlockSize")

    ("subblock" | vectorOfN(provide(framesBlockSize), ulong(framesBitsPerSample))).hlist
  }.as[SubframeVerbatim]

}