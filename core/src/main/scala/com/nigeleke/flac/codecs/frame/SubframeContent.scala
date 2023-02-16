package com.nigeleke.flac
package codecs
package frame

import metadata.StreamInfo

import scodec.*
import scodec.codecs.*

trait SubframeContent

object SubframeContent:

  enum Type:
    case Constant, Verbatim, Fixed, Lpc

  //  SUBFRAME_CONSTANT
  //  || SUBFRAME_FIXED
  //    || SUBFRAME_LPC
  //    || SUBFRAME_VERBATIM
  //
  def codec(channelNumber: Int)(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ): Codec[SubframeContent] =

    val subframeType: Type = subframeHeader.codedSubframeType match
      case 0                      => Type.Constant
      case 1                      => Type.Verbatim
      case n if 8 <= n && n <= 12 => Type.Fixed
      case n if n >= 32           => Type.Lpc

    discriminated[SubframeContent]
      .by(provide(subframeType))
      .typecase(Type.Constant, SubframeConstant.codec)
      .typecase(Type.Verbatim, SubframeVerbatim.codec)
      .typecase(Type.Fixed, SubframeFixed.codec(channelNumber))
      .typecase(Type.Lpc, SubframeLpc.codec(channelNumber))
      .as[SubframeContent]
