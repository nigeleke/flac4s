package com.nigeleke.flac
package codecs
package metadata

import scodec.*
import scodec.bits.*
import scodec.codecs.*

import scala.annotation.tailrec

type MetadataBlocks = List[MetadataBlock]

object MetadataBlocks:
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
  //
  // List[MetadataBlock] is NonEmpty as StreamInfo is a mandatory first block.
  //
  val codec: Codec[MetadataBlocks] = 
    MetadataBlock.codec.consume(block =>
      if block.header.isLast
      then provide(List(block))
      else codec.xmap(blocks => block +: blocks, blocks => blocks.tail)
    )(blocks => blocks.head)
