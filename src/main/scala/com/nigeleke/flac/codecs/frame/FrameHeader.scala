package com.nigeleke.flac.codecs.frame

import scodec._
import scodec.bits._
import scodec.codecs._
import scodec.codecs.literals._
import shapeless.{::, HNil}

case class FrameHeader(blockingStrategy: Int,
                       codedBlockSize: Int,
                       codedSampleRate: Int,
                       channelAssignment: Int,
                       codedSampleSize: Int,
                       // TODO: Determine how this is decoded in the spec...
                       //                       codedSampleNumber: Option[String],
                       //                       codedFrameNumber: Option[String],
                       blockSize8Bit: Option[Int],
                       blockSize16Bit: Option[Int],
                       sampleRate8BitKHz: Option[Int],
                       sampleRate16BitHz: Option[Int],
                       sampleRate16BitDHz: Option[Int],
                       crc: Int)

object FrameHeader {

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
  val codec : Codec[FrameHeader] = {

    ("sync" | bin"11111111111110") ::
      ("reserved" | bin"0") ::
      ("blockingStrategy" | uint(1)) ::
      ("codedBlockSize" | uint(4)) ::
      ("codedSampleRate" | uint(4)) flatConcat {
        case _ :: _ :: blockingStrategy :: codedBlockSize :: codedSampleRate :: HNil =>

        val isVariableBlockSize = blockingStrategy == 1
        println(s"FrameHeader:: isVariableBlockSize: $isVariableBlockSize codedBlockSize: $codedBlockSize codedSampleRate: $codedSampleRate")

        ("channelAssignment" | uint(4)) ::
          ("codedSampleSize" | uint(3)) ::
          ("reserved" | bin"0") ::
          // TODO: Determine what this means in the spec...
          //          ("codedSampleNumber" | conditional(isVariableBlockSize, paddedFixedSizeBits(56, utf8, bin"0"))) ::
          //          ("codedFrameNumber" | conditional(!isVariableBlockSize, utf8)) ::
          //
          ("blockSize8Bit" | conditional(codedBlockSize == 6, uint8)) ::
          ("blockSize16Bit" | conditional(codedBlockSize == 7, uint16)) ::
          ("sampleRate8BitKHz" | conditional(codedSampleRate == 12, uint8)) ::
          ("sampleRate16BitHz" | conditional(codedSampleRate == 13, uint16)) ::
          ("sampleRate16BitDHz" | conditional(codedSampleRate == 14, uint16)) ::
          ("notUnderstood" | hex"aaaa") ::
          ("crc" | uint8)
    }

  }.as[FrameHeader]

}
