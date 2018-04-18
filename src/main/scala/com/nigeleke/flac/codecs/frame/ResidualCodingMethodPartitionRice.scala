package com.nigeleke.flac.codecs.frame

import scodec._
import scodec.codecs._

case class ResidualCodingMethodPartitionRice(order: Int, partitions: Vector[RicePartition]) extends Residual

object ResidualCodingMethodPartitionRice {

  //  <4>	Partition order.
  //    RICE_PARTITION+	There will be 2^order partitions.
  //
  val codec : Codec[ResidualCodingMethodPartitionRice] = {
    ("order" | uint(4)) flatPrepend { order =>
      val n = 1 << order
      ("partitions" | vectorOfN(provide(n), RicePartition.codec)).hlist
    }
  }.as[ResidualCodingMethodPartitionRice]

}
