package com.nigeleke.flac.codecs

import com.nigeleke.flac.codecs.metadata._

package object frame {

  //  <6>	  Subframe type:
  //        ...
  //        1xxxxx : SUBFRAME_LPC, xxxxx=order-1
  //
  def lpcOrder(implicit header: SubframeHeader) : Int = {
    val subframeType = header.codedSubframeType

    if (subframeType >= 0x20) (subframeType & 0x1F) + 1
    else 0
  }

  //  <6>	  Subframe type:
  //        ...
  //        001xxx : if(xxx <= 4) SUBFRAME_FIXED, xxx=order ; else reserved
  //        ...
  //
  def fixedOrder(implicit header: SubframeHeader) : Int = {
    val subframeType = header.codedSubframeType
    val order = subframeType & 7

    if (8 <= subframeType && subframeType <= 15 && order <= 4) order
    else 0
  }

  //    <4>   Block size in inter-channel samples:
  //          0000 : reserved
  //          0001 : 192 samples
  //          0010-0101 : 576 * (2^(n-2)) samples, i.e. 576/1152/2304/4608
  //          0110 : get 8 bit (blocksize-1) from end of header
  //          0111 : get 16 bit (blocksize-1) from end of header
  //          1000-1111 : 256 * (2^(n-8)) samples, i.e. 256/512/1024/2048/4096/8192/16384/32768
  //
  def framesBlockSize(implicit header: FrameHeader) : Int = header.codedBlockSize match {
    case 1 => 192
    case n if 2 <= n && n <= 5 => 576 << (n-2)
    case 6 => header.blockSize8Bit.get + 1
    case 7 => header.blockSize16Bit.get + 1
    case n if 8 <= n && n <= 15 => 256 << (n-8)
    case n => throw new IllegalArgumentException(s"FrameHeader blockSize code is invalid - is $n, should be between 1 & 15")
  }

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
  //
  def sampleRate(implicit header: FrameHeader, streamInfo: StreamInfo) : Int = header.codedSampleRate match {
    case 0 => streamInfo.sampleRate
    case 1 => 88200
    case 2 => 176400
    case 3 => 192000
    case 4 => 8000
    case 5 => 16000
    case 6 => 22050
    case 7 => 24000
    case 8 => 32000
    case 9 => 44100
    case 10 => 48000
    case 11 => 96000
    case 12 => header.sampleRate8BitKHz.get * 1000
    case 13 => header.sampleRate16BitHz.get
    case 14 => header.sampleRate16BitDHz.get * 10
    case n => throw new IllegalArgumentException(s"FrameHeader sampleRate code is invalid - is $n, should be between 0 & 14")
  }

  //    <3>   Sample size in bits:
  //          000 : get from STREAMINFO metadata block
  //          001 : 8 bits per sample
  //          010 : 12 bits per sample
  //          011 : reserved
  //          100 : 16 bits per sample
  //          101 : 20 bits per sample
  //          110 : 24 bits per sample
  //          111 : reserved
  //
  def framesBitsPerSample(implicit header: FrameHeader, streamInfo: StreamInfo) : Int = header.codedSampleSize match {
    case 0 => streamInfo.bitsPerSample + 1
    case 1 => 8
    case 2 => 12
    case 4 => 16
    case 5 => 20
    case 6 => 24
    case n => throw new IllegalArgumentException(s"FrameHeader bitsPerSample code is invalid - is $n, should be one of 0, 1, 2, 4, 5, 6")
  }

}
