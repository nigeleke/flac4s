//package com.nigeleke.flac
//package codecs
//package frame
//
//import scodec.*
//import scodec.bits.*
//import scodec.codecs.*
//
//final case class Residual(
//    residualCodingMethod: Int,
//    partitionOrder: Int,
//    partitions: Vector[Partition]
//)
//
//object Residual:
//
//  //  <2>	Residual coding method:
//  //      00 : partitioned Rice coding with 4-bit Rice parameter; RESIDUAL_CODING_METHOD_PARTITIONED_RICE follows
//  //      01 : partitioned Rice coding with 5-bit Rice parameter; RESIDUAL_CODING_METHOD_PARTITIONED_RICE2 follows
//  //      10-11 : reserved
//  //  RESIDUAL_CODING_METHOD_PARTITIONED_RICE ||
//  //    RESIDUAL_CODING_METHOD_PARTITIONED_RICE2
//  //  <4>	Partition order.
//  //  <4(+5)>	Encoding parameter:
//  //    0000-1110 : Rice parameter.
//  //    1111 : Escape code, meaning the partition is in unencoded binary form using n bits per sample; n follows as a 5-bit number.
//  //  RICE_PARTITION+	There will be 2^order partitions.
//  //
//  private val residualCodingMethodCodec = log("residualCodingMethod")(uint2)
//
//  private val partitionOrderCodec = log("partitionOrder")(uint4)
//
//  def codec(predictorOrder: Int)(using fh: FrameHeader): Codec[Residual] = {
//
//    println(s"***** ***** Decoding residuals for $predictorOrder $fh")
//
//    (residualCodingMethodCodec :: partitionOrderCodec)
//      .flatConcat { case (residualCodingMethod, partitionOrder) =>
//        val riceParameterSize = if residualCodingMethod == 0 then 4 else 5
//        val nPartitions       = 1 << partitionOrder
//        val partitionCodec = Partition.codec(_, riceParameterSize, partitionOrder, predictorOrder)
//
//        val headCodec = log("firstPartition")(partitionCodec(true))
//        val tailCodec =
//          log("otherPartitions")(vectorOfN(provide(nPartitions - 1), partitionCodec(false)))
//        val partitionsCodec =
//          log("partitions") {
//            (headCodec :: tailCodec).xmap(
//              (p, ps) => (p +: ps.toList).toVector,
//              ps => (ps.head, ps.tail)
//            )
//          }
//        partitionsCodec.tuple
//      }
//  }.dropUnits.as[Residual]
