package com.nigeleke.flac
package codecs

import scodec.*
import scodec.Attempt.*
import scodec.bits.*
import scodec.codecs.*

import scala.annotation.tailrec

package object frame:

  @tailrec
  private def decodeUnaryAndCount(bits: BitVector, count: Int): Attempt[DecodeResult[Int]] =
    val result = bool.decode(bits).require
    if (result.value) Successful(DecodeResult(count, result.remainder))
    else decodeUnaryAndCount(result.remainder, count + 1)

  val unaryCodec: Codec[Int] =
    new Codec[Int]:
      override def decode(bits: BitVector): Attempt[DecodeResult[Int]] =
        decodeUnaryAndCount(bits, 0)

      override def encode(value: Int): Attempt[BitVector] = ???

      override def sizeBound: SizeBound = SizeBound.atLeast(1)

  val wastedCodec: Codec[Int] = log("wasted") {
    new Codec[Int]:
      override def decode(bits: BitVector): Attempt[DecodeResult[Int]] =
        val result = bool.decode(bits).require
        if (result.value) decodeUnaryAndCount(result.remainder, 1)
        else Successful(DecodeResult(0, result.remainder))

      override def encode(value: Int): Attempt[BitVector] = ???

      override def sizeBound: SizeBound = SizeBound.atLeast(1)
  }
