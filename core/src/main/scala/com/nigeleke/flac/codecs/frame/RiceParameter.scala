//package com.nigeleke.flac
//package codecs
//package frame
//
//import scodec.*
//import scodec.bits.*
//import scodec.codecs.*
//
//case class RiceParameter(codedValue: Int, maybeEscapedValue: Option[Int]):
//  override def toString = s"rice($codedValue, $maybeEscapedValue)"
//
//object RiceParameter:
//
//  def codec(riceParameterSize: Int): Codec[RiceParameter] =
//    val escapeValue       = (1 << riceParameterSize) - 1
//    val escapedValueCodec = log("escapedValue")(uint(5))
//    log("riceParameter") {
//      uint(riceParameterSize).flatZip { codedValue =>
//        conditional(codedValue == escapeValue, escapedValueCodec)
//      }
//    }.dropUnits.as[RiceParameter]
//
//  extension (riceParameter: RiceParameter)
//    def encodingLength = riceParameter.maybeEscapedValue.getOrElse(riceParameter.codedValue)
