package com.nigeleke.flac
package codecs
package frame

import com.nigeleke.flac.codecs.frame.FrameHeader.ChannelAssignment.Independent3
import metadata.*
import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class FrameHeader(
    blockingStrategy: FrameHeader.BlockingStrategy,
    blockSizeBits: Int,
    sampleRateBits: Int,
    channelBits: FrameHeader.ChannelAssignment,
    bitDepthBits: Int,
    codedNumber: String,
    maybeUncommonBlockSize: Option[Int],
    maybeUncommonSampleRate: Option[Int],
    crc: Int
)

object FrameHeader:
  //    <14>  Sync code '11111111111110'
  //    <1>   Reserved: [1]
  //          0 : mandatory value
  //          1 : reserved for future use
  //    <1>   Blocking strategy: [2] [3]
  //          0 : fixed-blocksize stream; frame header encodes the frame number
  //          1 : variable-blocksize stream; frame header encodes the sample number
  //    <4>   Block size in inter-channel samples:
  //          0000 : reserved
  //          0001 : 192 samples
  //          0010-0101 : 576 * (2^(n-2)) samples, i.e. 576/1152/2304/4608
  //          0110 : get 8 bit (blocksize-1) from end of header
  //          0111 : get 16 bit (blocksize-1) from end of header
  //          1000-1111 : 256 * (2^(n-8)) samples, i.e. 256/512/1024/2048/4096/8192/16384/32768
  //    <4>   Sample rate:
  //          0000 : get from STREAMINFO metadata block
  //          0001 : 88.2kHz
  //          0010 : 176.4kHz
  //          0011 : 192kHz
  //          0100 : 8kHz
  //          0101 : 16kHz
  //          0110 : 22.05kHz
  //          0111 : 24kHz
  //          1000 : 32kHz
  //          1001 : 44.1kHz
  //          1010 : 48kHz
  //          1011 : 96kHz
  //          1100 : get 8 bit sample rate (in kHz) from end of header
  //          1101 : get 16 bit sample rate (in Hz) from end of header
  //          1110 : get 16 bit sample rate (in tens of Hz) from end of header
  //          1111 : invalid, to prevent sync-fooling string of 1s
  //    <4>   Channel assignment
  //          0000-0111 : (number of independent channels)-1. Where defined, the channel order follows SMPTE/ITU-R recommendations. The assignments are as follows:
  //          1 channel: mono
  //          2 channels: left, right
  //          3 channels: left, right, center
  //          4 channels: front left, front right, back left, back right
  //          5 channels: front left, front right, front center, back/surround left, back/surround right
  //          6 channels: front left, front right, front center, LFE, back/surround left, back/surround right
  //          7 channels: front left, front right, front center, LFE, back center, side left, side right
  //          8 channels: front left, front right, front center, LFE, back left, back right, side left, side right
  //          1000 : left/side stereo: channel 0 is the left channel, channel 1 is the side(difference) channel
  //          1001 : right/side stereo: channel 0 is the side(difference) channel, channel 1 is the right channel
  //          1010 : mid/side stereo: channel 0 is the mid(average) channel, channel 1 is the side(difference) channel
  //          1011-1111 : reserved
  //    <3>   Sample size in bits:
  //          000 : get from STREAMINFO metadata block
  //          001 : 8 bits per sample
  //          010 : 12 bits per sample
  //          011 : reserved
  //          100 : 16 bits per sample
  //          101 : 20 bits per sample
  //          110 : 24 bits per sample
  //          111 : reserved
  //    <1>   Reserved:
  //          0 : mandatory value
  //          1 : reserved for future use
  //    <?>   if(variable blocksize)
  //            <8-56>:"UTF-8" coded sample number (decoded number is 36 bits) [4]
  //          else
  //            <8-48>:"UTF-8" coded frame number (decoded number is 31 bits) [4]
  //    <?>   if(blocksize bits == 011x)
  //            8/16 bit (blocksize-1)
  //    <?>   if(sample rate bits == 11xx)
  //            8/16 bit sample rate
  //    <8>	  CRC-8 (polynomial = x^8 + x^2 + x^1 + x^0, initialized with 0) of everything before the crc, including the sync code
  //

  enum BlockingStrategy:
    case Fixed, Variable

  enum ChannelAssignment:
    case Mono, Stereo, Independent3, Independent4,
      Independent5, Independent6, Independent7, Independent8,
      LeftSide, RightSide, MidSide

  //          1 channel: mono
  //          2 channels: left, right
  //          3 channels: left, right, center
  //          4 channels: front left, front right, back left, back right
  //          5 channels: front left, front right, front center, back/surround left, back/surround right
  //          6 channels: front left, front right, front center, LFE, back/surround left, back/surround right
  //          7 channels: front left, front right, front center, LFE, back center, side left, side right
  //          8 channels: front left, front right, front center, LFE, back left, back right, side left, side right
  //          1000 : left/side stereo: channel 0 is the left channel, channel 1 is the side(difference) channel
  //          1001 : right/side stereo: channel 0 is the side(difference) channel, channel 1 is the right channel
  //          1010 : mid/side stereo: channel 0 is the mid(average) channel, channel 1 is the side(difference) channel
  //          1011-1111 : reserved

  private val syncCodec = log("sync")(constant(bin"111111111111100"))

  private val blockingStrategyCodec =
    log("blockStrategy")(uint(1).xmap(BlockingStrategy.fromOrdinal, _.ordinal))

  private val blockSizeBitsCodec  = log("blockSizeBits")(uint4)
  private val sampleRateBitsCodec = log("sampleRateBits")(uint4)

  private val channelBitsCodec =
    log("channelBits")(uint4).xmap(ChannelAssignment.fromOrdinal, _.ordinal)

  private val bitDepthBitsCodec = log("bitDepthBits")(uint(3))
  private val reservedCodec     = log("reserved1")(constant(bin"0"))

  private def codedNumberCodec(blockingStrategy: BlockingStrategy) = log("codedNumber") {
    if blockingStrategy == BlockingStrategy.Variable
    then fixedSizeBits(36, utf8)
    else fixedSizeBits(8, utf8) // One byte used for frame#
  }

  private def uncommonBlockSizeCodec(blockSizeBits: Int) = log("uncommonBlockSize") {
    blockSizeBits match
      case 6 => ("uncommonBlockSize" | conditional(true, uint8.xmap(_ + 1, _ - 1)))
      case 7 => ("uncommonBlockSize" | conditional(true, uint16.xmap(_ + 1, _ - 1)))
      case _ => ("uncommonBlockSize" | conditional(false, uint8))
  }

  private def uncommonSampleRateCodec(sampleRateBits: Int) = log("uncommonSampleRate") {
    sampleRateBits match
      case 12 => ("uncommonSampleRate" | conditional(true, uint8))
      case 13 => ("uncommonSampleRate" | conditional(true, uint16))
      case 14 => ("uncommonSampleRate" | conditional(true, uint16))
      case _  => ("uncommonSampleRate" | conditional(false, uint8))
  }

  private val crcCodec = log("crc")(uint8)

  val codec: Codec[FrameHeader] =
    (syncCodec ::
      blockingStrategyCodec ::
      blockSizeBitsCodec ::
      sampleRateBitsCodec ::
      channelBitsCodec ::
      bitDepthBitsCodec ::
      reservedCodec).dropUnits
      .flatConcat {
        case (blockingStrategy, blockSizeBits, sampleRateBits, channelBits, bitDepthBits) =>
          codedNumberCodec(blockingStrategy) ::
            uncommonBlockSizeCodec(blockSizeBits) ::
            uncommonSampleRateCodec(sampleRateBits) ::
            crcCodec
      }
      .as[FrameHeader]

  extension (header: FrameHeader)
    def bitDepth(using streamInfo: StreamInfo) =
      header.bitDepthBits match
        case 0 => streamInfo.bitDepth
        case 1 => 8
        case 2 => 12
        case 4 => 16
        case 5 => 20
        case 6 => 24
        case _ => 0

    def blockSize =
      header.blockSizeBits match
        case 1                      => 192
        case n if 2 <= n && n <= 5  => 576 * (1 << (n - 2))
        case 6                      => header.maybeUncommonBlockSize.getOrElse(0)
        case 7                      => header.maybeUncommonBlockSize.getOrElse(0)
        case n if 8 <= n && n <= 15 => 256 * (1 << (n - 8))
        case _                      => 0

    def isSideChannel(channelNumber: Int): Boolean =
      // TODO: May need to look at WAVEFORMATEXTENSIBLE_CHANNEL_MASK in Vorbis comment
      //       fields for a re-mapped side channel...?
      header.channelBits match
        case ChannelAssignment.LeftSide  => channelNumber == 2
        case ChannelAssignment.RightSide => channelNumber == 1
        case ChannelAssignment.MidSide   => channelNumber == 2
        case _                           => false

    def sideChannelBitCount(channelNumber: Int): Int =
      if isSideChannel(channelNumber) then 1 else 0
