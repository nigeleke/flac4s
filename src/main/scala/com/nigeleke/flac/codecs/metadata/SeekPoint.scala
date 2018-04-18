package com.nigeleke.flac.codecs.metadata

import scodec._
import scodec.codecs._

case class SeekPoint(sampleNumber: Long, frameHeaderOffset: Long, samplesCount: Int)

object SeekPoint {

  //  <64>	Sample number of first sample in the target frame, or 0xFFFFFFFFFFFFFFFF for a placeholder point.
  //  <64>	Offset (in bytes) from the first byte of the first frame header to the first byte of the target frame's header.
  //  <16>	Number of samples in the target frame.
  //
  def codec : Codec[SeekPoint] = {
    ("sampleNumber" | int64) ::
      ("frameHeaderOffset" | int64) ::
      ("samplesCount" | uint16)
  }.as[SeekPoint]

}