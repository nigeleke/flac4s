package com.nigeleke.flac.codecs.metadata

import scodec._
import scodec.bits._

case class Padding(n: Int) extends MetadataBlock

object Padding {

  //  <n>   n '0' bits (n must be a multiple of 8)
  //
  def codec(n: Int) : Codec[Padding] = new Codec[Padding] {

    override def decode(bits: BitVector): Attempt[DecodeResult[Padding]] = ???

    override def encode(value: Padding): Attempt[BitVector] = ???

    override def sizeBound: SizeBound = {
      require(n % 8 == 0)
      SizeBound.exact(n)
    }

  }

}
