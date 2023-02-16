package com.nigeleke.flac
package codecs
package frame

import metadata.StreamInfo

import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class SubframeFixed(
    warmups: Vector[SubframeFixed.Warmup],
    residualCodingMethod: Int,
    partitionOrder: Int,
    parameters: Vector[SubframeFixed.Partition]
) extends SubframeContent

object SubframeFixed:
  case class Warmup(value: Int)
  case class Parameter(value: Int, maybeEscapedValue: Option[Int])
  case class Residual(quotient: Int, maybeRemainder: Option[Int]):
    override def toString =
      val remainder = maybeRemainder.map(_.toString).getOrElse("-")
      s"res(q:$quotient r:$remainder)"
  case class Partition(parameter: Parameter, residuals: Vector[Residual]):
    override def toString =
      val zipped     = residuals.zipWithIndex.map(ri => s"[${ri._2}]=${ri._1}")
      val zippedTop  = zipped.take(3).mkString(", ")
      val zippedTail = zipped.takeRight(3).mkString(", ")
      s"Partition($parameter), $zippedTop..$zippedTail)"

  //  <n>	Unencoded warm-up samples (n = frame's bits-per-sample * predictor order).
  //  RESIDUAL	Encoded residual
  //
  private def warmupCodec(nBits: Int) = codecs.int(nBits).as[Warmup]

  private def warmupsCodec(predictorOrder: Int, channelNumber: Int)(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ) = log("warmups") {
    val warmupBitsCount = frameHeader.bitDepth + frameHeader.sideChannelBitCount(channelNumber)
    println(s"warmupsBitsCount: $warmupBitsCount")
    vectorOfN(provide(predictorOrder), warmupCodec(warmupBitsCount))
  }.as[Vector[Warmup]]

  private val residualCodingMethodCodec = uint2

  private val partitionOrderCodec = uint4

  private def parameterCodec(riceParameterSize: Int): Codec[Parameter] =
    val escapeValue       = (1 << riceParameterSize) - 1
    val escapedValueCodec = uint(5)
    uint(riceParameterSize)
      .flatZip { riceParameter =>
        conditional(riceParameter == escapeValue, escapedValueCodec)
      }
      .dropUnits
      .as[Parameter]

  private def residualCodec(size: Int): Codec[Residual] =
    def toResidual(quotient: Int, maybeRemainder: Option[Int]): Residual =
      Residual(quotient, maybeRemainder)
    def fromResidual(residual: Residual): (Int, Option[Int]) = ???
    (unaryCodec :: conditional(size != 0, uint(size)))
      .xmap(toResidual, fromResidual)
      .as[Residual]

  private def residualsCodec(nResiduals: Int, riceParameter: Int): Codec[Vector[Residual]] =
    vectorOfN(provide(nResiduals), residualCodec(riceParameter))

  private def partitionCodec(riceParameterSize: Int, nResiduals: Int): Codec[Partition] =
    println(
      s"***** partitionCodec: riceParamSize $riceParameterSize nRes: $nResiduals"
    )
    parameterCodec(riceParameterSize)
      .flatZip { parameter =>
        println(s"***** partitionCodec: riceParameter $parameter")
        residualsCodec(nResiduals, parameter.value)
      }
      .as[Partition]

  private def partitionsCodec(
      nPartitions: Int,
      riceParameterSize: Int,
      nResiduals: Int
  ): Codec[Vector[Partition]] =
    println(
      s"***** partitionsCodec: riceParamSize $riceParameterSize nPart: $nPartitions nRes: $nResiduals"
    )
    vectorOfN(provide(nPartitions), partitionCodec(riceParameterSize, nResiduals))
      .as[Vector[Partition]]

  def codec(channelNumber: Int)(using
      streamInfo: StreamInfo,
      frameHeader: FrameHeader,
      subframeHeader: SubframeHeader
  ): Codec[SubframeFixed] = {
    val predictorOrder = subframeHeader.codedSubframeType - 8
    (warmupsCodec(predictorOrder, channelNumber) ::
      residualCodingMethodCodec :: partitionOrderCodec)
      .flatConcat { case (_, residualCodingMethod, partitionOrder) =>
        val riceParameterSize = if residualCodingMethod == 0 then 4 else 5
        val nPartitions       = 1 << partitionOrder
        val nResiduals        = frameHeader.blockSize - predictorOrder
        println(
          s"***** fixedCodec: riceParamSize $riceParameterSize nPart: $nPartitions nRes: $nResiduals"
        )
        partitionsCodec(nPartitions, riceParameterSize, nResiduals).tuple
      }
  }.as[SubframeFixed]
