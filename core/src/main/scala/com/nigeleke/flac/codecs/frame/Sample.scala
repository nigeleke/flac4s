//package com.nigeleke.flac
//package codecs
//package frame
//
//import scodec.*
//import scodec.codecs.*
//
//trait Sample
//
//object Sample:
//  final case class EncodedSample(quotient: Int, maybeRemainder: Option[Int]) extends Sample:
//    override def toString =
//      val remainder = maybeRemainder.map(_.toString).getOrElse("")
//      s"enc(q:$quotient,r:$remainder)"
//
//  final case class UnencodedSample(maybeValue: Option[Int]) extends Sample:
//    override def toString =
//      val value = maybeValue.map(_.toString).getOrElse("")
//      s"unenc($value)"
//
//  def codec(riceParameter: RiceParameter): Codec[Sample] =
//    val encodingLength = riceParameter.encodingLength
//    val definedLength  = encodingLength != 0
//
//    val quotientCodec      = unaryCodec
//    val remainderCodec     = conditional(definedLength, uint(encodingLength))
//    val encodedSampleCodec = (quotientCodec :: remainderCodec).as[EncodedSample]
//
//    def unencodedSampleCodec = conditional(definedLength, int(encodingLength)).as[UnencodedSample]
//
//    val escaped = riceParameter.maybeEscapedValue.isDefined
//    discriminated[Sample]
//      .by(provide(escaped))
//      .typecase(false, encodedSampleCodec)
//      .typecase(true, unencodedSampleCodec)
