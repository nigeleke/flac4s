package com.nigeleke.flac
package codecs
package metadata

import scodec.Attempt.Successful
import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class SeekTable(seekPoints: Vector[SeekPoint]) extends MetadataBlock.Data

object SeekTable:

  //  SEEKPOINT+	One or more seek points
  //
  def codec(metadataBlockLength: Int): Codec[SeekTable] = {
    new Codec[SeekTable]:

      val seekPointLength = 8 + 8 + 2

      override def decode(bits: BitVector): Attempt[DecodeResult[SeekTable]] =
        val pointsCount = metadataBlockLength / seekPointLength
        val points      = vectorOfN(provide(pointsCount), SeekPoint.codec).decode(bits).require
        Successful(DecodeResult(SeekTable(points.value), points.remainder))

      override def encode(value: SeekTable): Attempt[BitVector] = ???
      //      vec(codec).encode(value.seekPoints)
      //    }

      override def sizeBound: SizeBound = SizeBound.exact(metadataBlockLength / seekPointLength)
  }
