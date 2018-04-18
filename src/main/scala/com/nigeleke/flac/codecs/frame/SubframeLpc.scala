package com.nigeleke.flac.codecs.frame

import com.nigeleke.flac.codecs.metadata.StreamInfo
import scodec._
import scodec.bits._
import scodec.codecs._
import shapeless.{::, HNil}

case class SubframeLpc(warmUpSamples: Vector[Long],
                       linearPredictorPrecision: Int,
                       linearPredictorShift: Int,
                       predictorCoefficents: Vector[Long],
                       residual: Residual) extends SubframeContent

object SubframeLpc {

  //  <n>	Unencoded warm-up samples (n = frame's bits-per-sample * lpc order).
  //  <4>	(Quantized linear predictor coefficients' precision in bits)-1 (1111 = invalid).
  //  <5>	Quantized linear predictor coefficient shift needed in bits (NOTE: this number is signed two's-complement).
  //  <n>	Unencoded predictor coefficients (n = qlp coeff precision * lpc order) (NOTE: the coefficients are signed two's-complement).
  //  RESIDUAL	Encoded residual
  //
  def codec(implicit frameHeader: FrameHeader, subframeHeader: SubframeHeader, streamInfo: StreamInfo) : Codec[SubframeLpc] = {

    println(s"SubframeLpc:: framesBitsPerSample: $framesBitsPerSample lpcOrder: $lpcOrder")

    ("warmupSamples" | vectorOfN(provide(lpcOrder), ulong(framesBitsPerSample))) ::
      ("precision" | uint(4)) flatConcat { case _ :: precision :: HNil =>

      println(s"SubframeLpc:: precision: ${precision+1}")

      ("shift" | int(5)) ::
        ("predictorCoefficients" | vectorOfN(provide(lpcOrder), long(precision + 1))) ::
        ("BADCONSTANT" | constant(hex"aaaaaaaaaaaaaaaaa")) ::
        ("residual" | Residual.codec)

    }

  }.as[SubframeLpc]
}
