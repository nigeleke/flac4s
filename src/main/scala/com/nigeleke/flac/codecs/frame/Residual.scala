package com.nigeleke.flac.codecs.frame

import scodec._
import scodec.codecs._

trait Residual

object Residual {


  //  <2>	Residual coding method:
  //      00 : partitioned Rice coding with 4-bit Rice parameter; RESIDUAL_CODING_METHOD_PARTITIONED_RICE follows
  //      01 : partitioned Rice coding with 5-bit Rice parameter; RESIDUAL_CODING_METHOD_PARTITIONED_RICE2 follows
  //      10-11 : reserved
  //  RESIDUAL_CODING_METHOD_PARTITIONED_RICE ||
  //    RESIDUAL_CODING_METHOD_PARTITIONED_RICE2
  //
  val codec : Codec[Residual] = {
    discriminated[Residual].by(uint(2))
      .typecase(0, ResidualCodingMethodPartitionRice.codec)
      .typecase(1, ResidualCodingMethodPartitionRice2.codec)
  }.as[Residual]

}
