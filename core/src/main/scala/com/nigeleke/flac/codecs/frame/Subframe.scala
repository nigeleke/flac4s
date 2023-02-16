package com.nigeleke.flac
package codecs
package frame

import metadata.StreamInfo

import scodec.*
import scodec.codecs.*

final case class Subframe(header: SubframeHeader, content: SubframeContent)

object Subframe:

  //  SUBFRAME_HEADER
  //  SUBFRAME_CONSTANT
  //    || SUBFRAME_FIXED
  //    || SUBFRAME_LPC
  //    || SUBFRAME_VERBATIM	The SUBFRAME_HEADER specifies which one.
  //
  def codec(channelNumber: Int)(using StreamInfo, FrameHeader): Codec[Subframe] = log("subframe") {
    SubframeHeader.codec
      .flatZip { subframeHeader =>
        given SubframeHeader = subframeHeader
        SubframeContent.codec(channelNumber)
      }
  }.as[Subframe]
