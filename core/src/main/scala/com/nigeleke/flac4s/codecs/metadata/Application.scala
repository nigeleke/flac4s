package com.nigeleke.flac
package codecs
package metadata

import scodec.*
import scodec.bits.*
import scodec.codecs.*

final case class Application(applicationId: Long, data: ByteVector) extends MetadataBlock.Data

object Application:

  // <32>  Registered application ID. (Visit the registration page to register an ID with FLAC.)
  // <n>   Application data (n must be a multiple of 8)
  //
  private val applicationIdCodec              = uint32
  private def applicationDataCodec(size: Int) = codecs.bytes(size)

  def codec(using header: MetadataBlock.Header): Codec[Application] =
    (applicationIdCodec :: applicationDataCodec(header.length - 4)).as[Application]
