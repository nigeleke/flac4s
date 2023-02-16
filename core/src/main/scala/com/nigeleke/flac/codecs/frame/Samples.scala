//package com.nigeleke.flac.codecs.frame
//
//import scodec.codecs.*
//
//final case class Samples(samples: Vector[Sample]):
//  override def toString =
//    val n = math.min(samples.size, 3)
//    s"Samples(${samples.size} ${samples.take(n)} .. ${samples.takeRight(n)}"
//
//object Samples:
//  def codec(nSamples: Int, riceParameter: RiceParameter) =
//    vectorOfN(provide(nSamples), Sample.codec(riceParameter)).as[Samples]
