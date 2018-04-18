package com.nigeleke.flac.codecs.frame

import scodec._
import scodec.bits._
import scodec.codecs._

case class RicePartition(encodingParameter: Int, encodedResidual: Vector[Int])

object RicePartition {

  //  <4(+5)>	Encoding parameter:
  //    0000-1110 : Rice parameter.
  //    1111 : Escape code, meaning the partition is in unencoded binary form using n bits per sample; n follows as a 5-bit number.
  //  <?>	Encoded residual. The number of samples (n) in the partition is determined as follows:
  //    if the partition order is zero, n = frame's blocksize - predictor order
  //    else if this is not the first partition of the subframe, n = (frame's blocksize / (2^partition order))
  //    else n = (frame's blocksize / (2^partition order)) - predictor order
  //
  val codec : Codec[RicePartition] = {
    uint(4).consume[Int](n => if (n == 0xf) uint(5) else provide(n)){ _ match {
      case m if m <= 0xf => m
      case n => n
    }} ::
      ("encodedResidual" | encodedResidualCodec)
  }.as[RicePartition]

  private val encodedResidualCodec : Codec[Vector[Int]] = {
    vectorOfN(provide(2), uint16) // TODO
  }.as[Vector[Int]]

  def lookaheadIssue98Patch(codec:Codec[Unit]):Codec[Boolean] = new Codec[Boolean] {
    def encode(value: Boolean): Attempt[BitVector] = Attempt.successful(BitVector.empty) //workaround https://github.com/scodec/scodec/issues/98
    def sizeBound: SizeBound = SizeBound.unknown
    def decode(bits: BitVector): Attempt[DecodeResult[Boolean]] =
      codec.decode(bits).map { _.map { _ => true }.mapRemainder(_ => bits) }
        .recover { case _ => DecodeResult(false, bits) }
  }

}
