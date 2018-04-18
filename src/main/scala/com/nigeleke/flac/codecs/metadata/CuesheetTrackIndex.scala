package com.nigeleke.flac.codecs.metadata

import scodec._
import scodec.bits._
import scodec.codecs._
import scodec.codecs.literals._

case class CuesheetTrackIndex(offset: Long, index: Int)

object CuesheetTrackIndex {

  //  <64>  Offset in samples, relative to the track offset, of the index point. For CD-DA, the offset must be evenly divisible by 588 samples (588 samples = 44100 samples/sec * 1/75th of a sec). Note that the offset is from the beginning of the track, not the beginning of the audio data.
  //  <8>   The index point number. For CD-DA, an index number of 0 corresponds to the track pre-gap. The first index in a track must have a number of 0 or 1, and subsequently, index numbers must increase by 1. Index numbers must be unique within a track.
  //  <3*8> Reserved. All bits must be set to zero.
  //
  val codec : Codec[CuesheetTrackIndex] = {

    ("offset" | long(64)) ::
      ("index" | uint8) ::
      ("reserved" | hex"000000")

  }.as[CuesheetTrackIndex]

}