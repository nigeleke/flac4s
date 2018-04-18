package com.nigeleke.flac.codecs

import java.io.InputStream

import com.nigeleke.flac.codecs.frame.Frame
import com.nigeleke.flac.codecs.metadata.{MetadataBlock, StreamInfo}
import scodec._
import scodec.bits._
import scodec.codecs._
import scodec.stream.decode
import shapeless.{::, HNil}

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.Duration

case class Flac(metadata: List[MetadataBlock], frames: List[Frame])

object Flac {

  val codec : Codec[Flac] = {
    ("marker" | constant(ByteVector("fLaC".getBytes()))) ::
      ("metadata" | MetadataBlock.listCodec) flatConcat  { case _ :: blocks :: HNil =>
      implicit val streamInfo = blocks.head.asInstanceOf[StreamInfo]
      println(s"Flac::codec streamInfo: $streamInfo")
      ("frame" | list(Frame.codec)).hlist
    }
  }.as[Flac]

  def from(is: InputStream) : Option[Flac] = {
    val fFrom = futureFrom(is)
    Await.result(fFrom, Duration.Inf)
  }

  def futureFrom(is: InputStream) : Future[Option[Flac]] = {
    val promise = Promise[Option[Flac]]
    val streamDecoder = decode.once(com.nigeleke.flac.codecs.Flac.codec).decodeInputStream(is)
    streamDecoder.map(f => promise.success(Option(f))).run.unsafeRun()
    promise.future
  }

}
