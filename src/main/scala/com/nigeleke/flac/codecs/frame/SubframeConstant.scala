package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata.StreamInfo
import scodec._
import scodec.codecs._

case class SubframeConstant(constant: Long) extends SubframeContent

object SubframeConstant {

  //  <n>	Unencoded constant value of the subblock, n = frame's bits-per-sample.
  //
  def codec(implicit frameHeader: FrameHeader, streamInfo: StreamInfo) : Codec[SubframeConstant] = {
    println(s"SubframeConstant:: framesBitsPerSample: $framesBitsPerSample")

    ("constant" | ulong(framesBitsPerSample)).hlist
  }.as[SubframeConstant]

}