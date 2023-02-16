package com.nigeleke.flac
package codecs
package frame

import metadata.*

import scodec.*
import scodec.codecs.*

case class Frame(header: FrameHeader, subframes: Vector[Subframe], footer: FrameFooter)

object Frame:

  //  FRAME_HEADER
  //  SUBFRAME+	One SUBFRAME per channel.
  //  <?>	Zero-padding to byte alignment.
  //  FRAME_FOOTER
  //
  private def alignedFrameHeaderCodec = byteAligned(FrameHeader.codec)

  private def subframesCodec(using streamInfo: StreamInfo, frameHeader: FrameHeader) =
    val zero = provide(Vector.empty[Subframe])
    def appendSubframeCodec(
        vectorCodec: Codec[Vector[Subframe]],
        channelNumber: Int
    ): Codec[Vector[Subframe]] =
      println(s"***** ***** trying subframe $channelNumber")
      (vectorCodec :: Subframe.codec(channelNumber))
        .xmap(
          (sfs, sf) => sfs :+ sf,
          sfs => (sfs.init, sfs.last)
        )
    (1 to streamInfo.channelCount).foldLeft(zero)(appendSubframeCodec)

  private def alignedSubframesCodec(using StreamInfo, FrameHeader) = byteAligned(subframesCodec)

  private def alignedFrameFooterCodec = byteAligned(FrameFooter.codec)

  def codec(using streamInfo: StreamInfo): Codec[Frame] = log("frame") {
    alignedFrameHeaderCodec.flatPrepend { frameHeader =>
      given FrameHeader = frameHeader
      alignedSubframesCodec :: alignedFrameFooterCodec
    }
  }.as[Frame]
