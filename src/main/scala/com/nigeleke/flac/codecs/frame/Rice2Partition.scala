package com.nigeleke.flac.codecs.frame

import scodec._

case class Rice2Partition(encodingParameter: Int, encodedResidual: Int)

object Rice2Partition {

  val codec : Codec[Rice2Partition] = {
    ???
  }

}


