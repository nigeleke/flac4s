package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata.StreamInfo
import scodec.codecs._

case class SubframeFixed(warmupSamples: Vector[Long], residual: Residual) extends SubframeContent

object SubframeFixed {

  //  <n>	Unencoded warm-up samples (n = frame's bits-per-sample * predictor order).
  //  RESIDUAL	Encoded residual
  //
  def codec(implicit frameHeader: FrameHeader, subframeHeader: SubframeHeader, streamInfo: StreamInfo) = {
    println(s"SubframeFixed:: framesBitsPerSample: $framesBitsPerSample fixedOrder: $fixedOrder")
    ("warmupSamples" | vectorOfN(provide(fixedOrder), ulong(framesBitsPerSample))) ::
      ("residual" | Residual.codec)
  }.as[SubframeFixed]

}
