//package com.nigeleke.flac
//package codecs
//package frame
//
//import scodec.*
//import scodec.bits.*
//import scodec.codecs.*
//
//final case class Partition(riceParameter: RiceParameter, samples: Samples):
//  override def toString = s"Partition($riceParameter\n  $samples)\n"
//
//object Partition:
//
//  //  <?>	Encoded residual. The number of samples (n) in the partition is determined as follows:
//  //    if the partition order is zero, n = frame's blocksize - predictor order
//  //    else if this is not the first partition of the subframe, n = (frame's blocksize / (2^partition order))
//  //    else n = (frame's blocksize / (2^partition order)) - predictor order
//  //
//  def codec(
//      firstPartition: Boolean,
//      riceParameterSize: Int,
//      partitionOrder: Int,
//      predictorOrder: Int
//  )(using
//      frameHeader: FrameHeader
//  ): Codec[Partition] = {
//    /*
//     * Each partition contains a certain amount of residual samples.
//     *  The number of residual samples in
//     *  the first partition is equal to (block size >> partition order) - predictor order,
//     *  i.e. the block size divided by the number of partitions minus the predictor order.
//     *
//     *  In all other partitions the number of residual samples is equal to (block size >> partition order).
//     */
//    RiceParameter
//      .codec(riceParameterSize)
//      .flatZip { riceParameter =>
//        val nSamples = partitionOrder match
//          case 0                    => frameHeader.blockSize - predictorOrder
//          case n if !firstPartition => frameHeader.blockSize >> partitionOrder
//          case n if firstPartition  => (frameHeader.blockSize >> partitionOrder) - predictorOrder
//        Samples.codec(nSamples, riceParameter)
//      }
//  }.as[Partition]
