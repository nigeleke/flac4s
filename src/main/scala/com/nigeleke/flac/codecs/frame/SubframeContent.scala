package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata.StreamInfo
import scodec.Codec
import scodec.codecs.{discriminated, provide}

trait SubframeContent

object SubframeContent {

  //  SUBFRAME_CONSTANT
  //  || SUBFRAME_FIXED
  //    || SUBFRAME_LPC
  //    || SUBFRAME_VERBATIM
  //
  def codec(implicit streamInfo: StreamInfo, frameHeader: FrameHeader, subframeHeader: SubframeHeader) : Codec[SubframeContent] = {

    object SubframeContentType extends Enumeration {
      val UNKNOWN, CONSTANT, VERBATIM, FIXED, LPC = Value
    }

    val subframeType = subframeHeader.codedSubframeType match {
      case 0 => SubframeContentType.CONSTANT
      case 1 => SubframeContentType.VERBATIM
      case n if 8 <= n && n <= 15 => SubframeContentType.FIXED
      case n if n >= 32 => SubframeContentType.LPC
      case _ => SubframeContentType.UNKNOWN
    }

    println(s"SubframeContent::subframeType $subframeType")

    discriminated[SubframeContent].by(provide(subframeType))
      .typecase(SubframeContentType.CONSTANT, SubframeConstant.codec)
      .typecase(SubframeContentType.VERBATIM, SubframeVerbatim.codec)
      .typecase(SubframeContentType.FIXED, SubframeFixed.codec)
      .typecase(SubframeContentType.LPC, SubframeLpc.codec)

  }.as[SubframeContent]

}