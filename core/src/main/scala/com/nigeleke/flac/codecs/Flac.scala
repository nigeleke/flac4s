package com.nigeleke.flac
package codecs

import frame.*
import metadata.*

import java.io.InputStream
import scodec.*
import scodec.bits.*
import scodec.codecs.*
import scodec.Attempt.Failure
import scodec.Attempt.Successful

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.Duration

case class Flac(
    metadataBlocks: MetadataBlocks,
    audioFrames: Frames
)

object Flac:

  val codec: Codec[Flac] = {
    ("marker" | constant(ByteVector("fLaC".getBytes))) ::
      ("metadata" | MetadataBlocks.codec).flatZip { blocks =>
        given streamInfo: StreamInfo = blocks.head.data.asInstanceOf[StreamInfo]
        ("audioFrames" | Frames.codec)
      }
  }.dropUnits.as[Flac]

  def from(is: InputStream): Attempt[DecodeResult[Flac]] = codec.decode(BitVector(is.readAllBytes))
