package com.nigeleke.flac
package codecs
package metadata

import scodec.*
import scodec.codecs.*

case class SeekPoint(sampleNumber: Long, frameHeaderOffset: Long, samplesCount: Int)

object SeekPoint:

  //  <64>	Sample number of first sample in the target frame, or 0xFFFFFFFFFFFFFFFF for a placeholder point.
  //  <64>	Offset (in bytes) from the first byte of the first frame header to the first byte of the target frame's header.
  //  <16>	Number of samples in the target frame.
  //
  private val sampleNumberCodec      = int64
  private val frameHeaderOffsetCodec = int64
  private val samplesCountCodec      = int16

  def codec: Codec[SeekPoint] = {
    sampleNumberCodec :: frameHeaderOffsetCodec :: samplesCountCodec
  }.dropUnits.as[SeekPoint]
