/*
 * Copyright (c) 2023, Nigel Eke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.nigeleke.flac4s
package codecs
package metadata

import logger.Logger.*

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
  private val minBlockSizeCodec = logField("minBlockSize")(uint16)
  private val maxBlockSizeCodec = logField("maxBlockSize")(uint16)
  private val minFrameSizeCodec = logField("minFrameSize")(uint24)
  private val maxFrameSizeCodec = logField("maxFrameSize")(uint24)
  private val channelCountCodec = logField("channelCount")(uint(3).xmap(_ + 1, _ - 1))
  private val sampleRateCodec   = logField("sampleRate")(uint(20))
  private val bitsDepthCodec    = logField("bitsDepth")(uint(5).xmap(_ + 1, _ - 1))
  private val samplesCountCodec = logField("samplesCount")(ulong(36))
  private val audioDataMd5Codec = logField("audioDataMd5")(codecs.bits(128))

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
