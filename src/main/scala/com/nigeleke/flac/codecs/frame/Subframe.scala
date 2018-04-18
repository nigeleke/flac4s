package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata.StreamInfo
import scodec._
import scodec.codecs._

case class Subframe(header: SubframeHeader, content: SubframeContent)

object Subframe {

  //  SUBFRAME_HEADER
  //  SUBFRAME_CONSTANT
  //    || SUBFRAME_FIXED
  //    || SUBFRAME_LPC
  //    || SUBFRAME_VERBATIM	The SUBFRAME_HEADER specifies which one.
  //
  def codec(implicit streamInfo: StreamInfo, frameHeader: FrameHeader) : Codec[Subframe] = {
    ("header" | SubframeHeader.codec) flatPrepend { implicit subframeHeader =>
      println(s"Subframe::codec frameHeader $frameHeader subframeHeader: $subframeHeader")
      SubframeContent.codec.hlist
    }
  }.as[Subframe]

}