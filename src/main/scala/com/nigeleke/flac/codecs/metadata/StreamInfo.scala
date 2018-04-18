package com.nigeleke.flac.codecs.metadata

import scodec._
import scodec.codecs._

case class StreamInfo(minBlockSize: Int,
                      maxBlockSize: Int,
                      minFrameSize: Int,
                      maxFrameSize: Int,
                      sampleRate: Int,
                      channelsCount: Int,
                      bitsPerSample: Int,
                      samplesCount: Long,
                      audioDataMd5: (Long, Long)) extends MetadataBlock

object StreamInfo {

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
  val codec : Codec[StreamInfo] = {
    ("minBlockSize" | uint16) ::
      ("maxBlockSize" | uint16) ::
      ("minFrameSize" | uint24) ::
      ("maxFrameSize" | uint24) ::
      ("sampleRate" | uint(20)) ::
      ("channelsCount" | uint(3)) ::
      ("codedSampleSize" | uint(5)) ::
      ("samplesCount" | ulong(36)) ::
      ("md5" | (int64 ~ int64))
  }.as[StreamInfo]

}
