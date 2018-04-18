package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata._
import scodec._
import scodec.codecs._

import scala.languageFeature.implicitConversions

case class Frame(header: FrameHeader, subframes: Vector[Subframe], footer: FrameFooter)

object Frame {

  //  FRAME_HEADER
  //  SUBFRAME+	One SUBFRAME per channel.
  //  <?>	Zero-padding to byte alignment.
  //  FRAME_FOOTER
  //
  def codec(implicit streamInfo: StreamInfo) : Codec[Frame] = {
    ("header" | FrameHeader.codec) flatPrepend { implicit header =>
      println(s"Frame:: FrameHeader: $header Channels#: $numberOfChannels")
      ("subframes" | vectorOfN(provide(numberOfChannels), Subframe.codec)) ::
        ("footer" | byteAligned(FrameFooter.codec))
    }
  }.as[Frame]

}
