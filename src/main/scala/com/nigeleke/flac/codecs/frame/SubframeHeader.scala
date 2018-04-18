package com.nigeleke.flac.codecs.frame

import scodec.Attempt.Successful
import scodec._
import scodec.bits._
import scodec.codecs._
import scodec.codecs.literals._

import scala.annotation.tailrec

case class SubframeHeader(codedSubframeType: Int, waste: Int)

object SubframeHeader {

  private val wasteCodec : Codec[Int] = new Codec[Int] {

    override def decode(bits: BitVector): Attempt[DecodeResult[Int]] = {
      val wasted = bool.decode(bits).require
      println(s"wasted1: ${wasted.value}")
      if (wasted.value) decode(wasted.remainder, 1)
      else Successful(DecodeResult(0, wasted.remainder))
    }

    @tailrec
    private def decode(bits: BitVector, count: Int)  : Attempt[DecodeResult[Int]] = {
      val wasted = bool.decode(bits).require
      println(s"wasted2: ${wasted.value}")
      if (wasted.value) Successful(DecodeResult(count, wasted.remainder))
      else decode(wasted.remainder, count + 1)
    }

    override def encode(value: Int): Attempt[BitVector] = ???

    override def sizeBound: SizeBound = SizeBound.atLeast(1)
  }

  //  <1>	  Zero bit padding, to prevent sync-fooling string of 1s
  //  <6>	  Subframe type:
  //        000000 : SUBFRAME_CONSTANT
  //        000001 : SUBFRAME_VERBATIM
  //        00001x : reserved
  //        0001xx : reserved
  //        001xxx : if(xxx <= 4) SUBFRAME_FIXED, xxx=order ; else reserved
  //        01xxxx : reserved
  //        1xxxxx : SUBFRAME_LPC, xxxxx=order-1
  //  <1+k>	'Wasted bits-per-sample' flag:
  //        0 : no wasted bits-per-sample in source subblock, k=0
  //        1 : k wasted bits-per-sample in source subblock, k-1 follows, unary coded; e.g. k=3 => 001 follows, k=7 => 0000001 follows.
  //
  val codec : Codec[SubframeHeader] = {
    ("padding" | bin"0") ::
      ("codedSubframeType" | uint(6)) ::
      ("waste" | wasteCodec)
  }.as[SubframeHeader]

}
