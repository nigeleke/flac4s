package com.nigeleke.flac
package codecs
package metadata

import scodec.*
import scodec.bits.*
import scodec.codecs.*

final case class Padding(padding: ByteVector) extends MetadataBlock.Data

object Padding:

  //  <n>   n '0' bits (n must be a multiple of 8)
  //
  def codec(n: Int): Codec[Padding] =
    bytes(n).as[Padding]
