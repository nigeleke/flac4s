package com.nigeleke.flac.codecs.frame

import scodec._
import scodec.codecs._

case class ResidualCodingMethodPartitionRice2(order: Int, partitions: Vector[Rice2Partition]) extends Residual

object ResidualCodingMethodPartitionRice2 {

  //  <4>	Partition order.
  //    RICE2_PARTITION+	There will be 2^order partitions.
  //
  val codec : Codec[ResidualCodingMethodPartitionRice2] = {
    ("order" | uint(4)) flatPrepend { order =>
      val n = 1 << order
      ("partitions" | vectorOfN(provide(n), Rice2Partition.codec)).hlist
    }
  }.as[ResidualCodingMethodPartitionRice2]

}

