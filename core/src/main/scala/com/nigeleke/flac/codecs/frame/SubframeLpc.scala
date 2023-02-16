package com.nigeleke.flac
package codecs
package frame

import metadata.StreamInfo

import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class SubframeLpc(
    warmUpSamples: Vector[BitVector],
    linearPredictorPrecision: Int,
    linearPredictorShift: Int,
    predictorCoefficents: Vector[Long],
    residual: Int
) extends SubframeContent

object SubframeLpc:

  //  <n>	Unencoded warm-up samples (n = frame's bits-per-sample * lpc order).
  //  <4>	(Quantized linear predictor coefficients' precision in bits)-1 (1111 = invalid).
  //  <5>	Quantized linear predictor coefficient shift needed in bits (NOTE: this number is signed two's-complement).
  //  <n>	Unencoded predictor coefficients (n = qlp coeff precision * lpc order) (NOTE: the coefficients are signed two's-complement).
  //  RESIDUAL	Encoded residual
  //
  private def warmupCodec(nBits: Int) = log("warmup")(codecs.bits(nBits))

  private def warmupsCodec(predictorOrder: Int, channelNumber: Int)(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ) = log("warmups") {
    val warmupBitsCount = frameHeader.bitDepth + frameHeader.sideChannelBitCount(channelNumber)
    vectorOfN(provide(predictorOrder), warmupCodec(warmupBitsCount))
  }.as[Vector[BitVector]]

  private val precisionCodec = log("precision")(uint4).xmap(_ + 1, _ - 1)

  private val rightShiftCodec = log("rightShift")(int(5))

  private def predictorCoefficientsCodec(lpcOrder: Int, precision: Int) =
    log("predictorCoefficients")(vectorOfN(provide(lpcOrder), long(precision)))

  def codec(channelNumber: Int)(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ): Codec[SubframeLpc] = {
    val lpcOrder = subframeHeader.codedSubframeType - 31
    warmupsCodec(lpcOrder, channelNumber) ::
      precisionCodec ::
      rightShiftCodec flatConcat { case (_, precision, rightShift) =>
        predictorCoefficientsCodec(lpcOrder, precision) ::
          ("residual" | provide(42))
      }
  }.as[SubframeLpc]
