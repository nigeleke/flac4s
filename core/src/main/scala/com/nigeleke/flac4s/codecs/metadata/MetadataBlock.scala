package com.nigeleke.flac
package codecs
package metadata

import scodec.Attempt.Successful
import scodec.*
import scodec.bits.*
import scodec.codecs.*

import scala.annotation.tailrec

final case class MetadataBlock(
    header: MetadataBlock.Header,
    data: MetadataBlock.Data
)

object MetadataBlock:

  enum Type:
    case StreamInfo, Padding, Application, SeekTable, VorbisComment, Cuesheet, Picture

  final case class Header(isLast: Boolean, blockType: Type, length: Int)
  trait Data

  private val isLastCodec = bool

  private val blockTypeCodec =
    (
      logToStdOut(uint(7).xmap(Type.fromOrdinal, _.ordinal), "***** btf")
    )
  private val lengthCodec = uint24

  private val headerCodec = {
    isLastCodec :: blockTypeCodec :: lengthCodec
  }.as[Header]

  //  <1>   Last-metadata-block flag: '1' if this block is the last metadata block before the audio blocks, '0' otherwise.
  //  <7>	  BLOCK_TYPE
  //        0 : STREAMINFO
  //        1 : PADDING
  //        2 : APPLICATION
  //        3 : SEEKTABLE
  //        4 : VORBIS_COMMENT
  //        5 : CUESHEET
  //        6 : PICTURE
  //        7-126 : reserved
  //        127 : invalid, to avoid confusion with a frame sync code
  //  <24>	Length (in bytes) of metadata to follow (does not include the size of the METADATA_BLOCK_HEADER)
  private def dataCodec(header: Header): Codec[Data] =
    given Header = header
    println(s"header: $header")
    discriminated[Data]
      .by(provide(header.blockType))
      .typecase(Type.Application, Application.codec)
      .typecase(Type.Cuesheet, Cuesheet.codec)
      .typecase(Type.Padding, Padding.codec(header.length))
      .typecase(Type.Picture, Picture.codec)
      .typecase(Type.SeekTable, SeekTable.codec(header.length))
      .typecase(Type.StreamInfo, StreamInfo.codec)
      .typecase(Type.VorbisComment, VorbisComment.codec)

  val codec: Codec[MetadataBlock] = log("metadataBlock") {
    headerCodec flatZip dataCodec
  }.dropUnits.as[MetadataBlock]
