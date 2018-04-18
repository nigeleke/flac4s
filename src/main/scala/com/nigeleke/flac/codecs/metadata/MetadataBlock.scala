package com.nigeleke.flac.codecs.metadata

import com.nigeleke.flac.codecs._
import scodec.Attempt.Successful
import scodec._
import scodec.bits._
import scodec.codecs._
import shapeless.{::, HNil}

import scala.annotation.tailrec

trait MetadataBlock

object MetadataBlock {

  private def codec(blockTypeId: Int, length: Int): Codec[MetadataBlock] = {
    object MetadataBlockType extends Enumeration {
      val STREAMINFO, PADDING, APPLICATION, SEEKTABLE, VORBIS_COMMENT, CUESHEET, PICTURE = Value
    }

    val blockType = MetadataBlockType(blockTypeId)

    println(s"MetadataBlockCodec: $blockType $length")

    discriminated[MetadataBlock].by(provide(blockType))
      .typecase(MetadataBlockType.APPLICATION, Application.codec)
      .typecase(MetadataBlockType.CUESHEET, Cuesheet.codec(length))
      .typecase(MetadataBlockType.PADDING, Padding.codec(length))
      .typecase(MetadataBlockType.PICTURE, Picture.codec)
      .typecase(MetadataBlockType.SEEKTABLE, SeekTable.codec(length))
      .typecase(MetadataBlockType.STREAMINFO, StreamInfo.codec)
      .typecase(MetadataBlockType.VORBIS_COMMENT, VorbisComment.codec)
  }

  val listCodec : Codec[List[MetadataBlock]] = new Codec[List[MetadataBlock]] {

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
    case class MarkedBlock(isLast: Boolean, blockType: Int, length: Int, block: MetadataBlock)
    val markedCodec: Codec[MarkedBlock] = {
      ("isLast" | bool) ::
        ("blockType" | uint(7)) ::
        ("length" | uint24) flatConcat { case _ :: blockType :: length :: HNil =>
        ("block" | MetadataBlock.codec(blockType, length)).hlist
      }
    }.as[MarkedBlock]

    override def encode(value: List[MetadataBlock]): Attempt[BitVector] = ???

    override def sizeBound: SizeBound = ???

    override def decode(bits: BitVector): Attempt[DecodeResult[List[MetadataBlock]]] = {
      val markedBlocks = decode(bits, List.empty)
      val remainder = markedBlocks.last.remainder
      val blocks = markedBlocks.map(mb => mb.value.block)
      Successful(DecodeResult(blocks, remainder))
    }

    private type ListDRMarkedBlock = List[DecodeResult[MarkedBlock]]

    @tailrec
    private def decode(bits: BitVector, markedBlocks: ListDRMarkedBlock) : ListDRMarkedBlock = {
      val markedBlock = markedCodec.decode(bits).require
      val markedBlocksInterim = markedBlocks :+ markedBlock

      if (markedBlock.value.isLast) markedBlocksInterim
      else decode(markedBlock.remainder, markedBlocksInterim)
    }
  }

//
//    override def encode(blocks: List[MetadataBlock]): Attempt[BitVector] = {
//      println("MetadataBlock::encode")
//      if (blocks.size == 0) {
//        Failure(Err("MetadataBlock::encode: Cannot encode zero length list"))
//      } else {
//        val zippedBlocks = blocks.zipWithIndex
//
//        val markedBlocks = zippedBlocks.map(bi => {
//          val isLast = bi._2 + 1 == blocks.size
//          val block = bi._1
//          MarkedBlock(isLast, block)
//        })
//
//        Codec.encodeSeq(markedCodec)(markedBlocks)
//      }
//    }
//
//    override def sizeBound: SizeBound = SizeBound.unknown

}
