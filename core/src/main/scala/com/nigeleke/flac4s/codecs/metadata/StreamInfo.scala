package com.nigeleke.flac
package codecs
package metadata

import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class StreamInfo(
    minBlockSize: Int,
    maxBlockSize: Int,
    minFrameSize: Int,
    maxFrameSize: Int,
    sampleRate: Int,
    channelCount: Int,
    bitDepth: Int,
    samplesCount: Long,
    audioDataMd5: BitVector
) extends MetadataBlock.Data

object StreamInfo:
  //  <16>  The minimum block size (in samples) used in the stream.
  //  <16>  The maximum block size (in samples) used in the stream. (Minimum blocksize == maximum blocksize) implies a fixed-blocksize stream.
  //  <24>  The minimum frame size (in bytes) used in the stream. May be 0 to imply the value is not known.
  //  <24>  The maximum frame size (in bytes) used in the stream. May be 0 to imply the value is not known.
  //  <20>  Sample rate in Hz. Though 20 bits are available, the maximum sample rate is limited by the structure of frame headers to 655350Hz. Also, a value of 0 is invalid.
  //  <3>   (number of channels)-1. FLAC supports from 1 to 8 channels
  //  <5>   (bits per sample)-1. FLAC supports from 4 to 32 bits per sample. Currently the reference encoder and decoders only support up to 24 bits per sample.
  //  <36>  Total samples in stream. 'Samples' means inter-channel sample, i.e. one second of 44.1Khz audio will have 44100 samples regardless of the number of channels. A value of zero here means the number of total samples is unknown.
  //  <128> MD5 signature of the unencoded audio data. This allows the decoder to determine if an error exists in the audio data even when the error does not result in an invalid bitstream.
  //
  private val minBlockSizeCodec = uint16
  private val maxBlockSizeCodec = uint16
  private val minFrameSizeCodec = uint24
  private val maxFrameSizeCodec = uint24
  private val channelCountCodec = uint(3).xmap(_ + 1, _ - 1)
  private val sampleRateCodec   = uint(20)
  private val bitsDepthCodec    = uint(5).xmap(_ + 1, _ - 1)
  private val samplesCountCodec = ulong(36)
  private val audioDataMd5Codec = codecs.bits(128)

  val codec: Codec[StreamInfo] = {
    minBlockSizeCodec ::
      maxBlockSizeCodec ::
      minFrameSizeCodec ::
      maxFrameSizeCodec ::
      sampleRateCodec ::
      channelCountCodec ::
      bitsDepthCodec ::
      samplesCountCodec ::
      audioDataMd5Codec
  }.as[StreamInfo]
