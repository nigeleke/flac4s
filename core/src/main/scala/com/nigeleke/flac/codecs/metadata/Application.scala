//package com.nigeleke.flac.codecs.metadata
//
//import scodec.*
//import scodec.bits.*
//
//case class Application(applicationId: Int, data: Vector[Byte]) extends MetadataBlock
//
//object Application {
//
//  // <32>  Registered application ID. (Visit the registration page to register an ID with FLAC.)
//  // <n>   Application data (n must be a multiple of 8)
//  //
//  val codec : Codec[Application] = new Codec[Application] {
//
//    private val errorMessage = "Vendor must provide codec in order to encode / decode this FlacApp instance."
//
//    override def decode(bits: BitVector): Attempt[DecodeResult[Application]] = Attempt.Failure(Err(errorMessage))
//    override def encode(value: Application): Attempt[BitVector] = Attempt.Failure(Err(errorMessage))
//    override def sizeBound: SizeBound = SizeBound.unknown
//
//  }
//
//}
