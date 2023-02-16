//package com.nigeleke.flac.codecs.metadata
//
//import scodec.*
//import scodec.bits.*
//import scodec.codecs.*
//
//case class Cuesheet(catalogNumber: String,
//                    leadInSamplesCount: Long,
//                    isCdCuesheet: Boolean,
//                    tracks: Vector[CuesheetTrack]) extends MetadataBlock
//
//object Cuesheet {
//
//  //  <128*8>         Media catalog number, in ASCII printable characters 0x20-0x7e. In general, the media catalog number may be 0 to 128 bytes long; any unused characters should be right-padded with NUL characters. For CD-DA, this is a thirteen digit number, followed by 115 NUL bytes.
//  //  <64>            The number of lead-in samples. This field has meaning only for CD-DA cuesheets; for other uses it should be 0. For CD-DA, the lead-in is the TRACK 00 area where the table of contents is stored; more precisely, it is the number of samples from the first sample of the media to the first sample of the first index point of the first track. According to the Red Book, the lead-in must be silence and CD grabbing software does not usually store it; additionally, the lead-in must be at least two seconds but may be longer. For these reasons the lead-in length is stored here so that the absolute position of the first track can be computed. Note that the lead-in stored here is the number of samples up to the first index point of the first track, not necessarily to INDEX 01 of the first track; even the first track may have INDEX 00 data.
//  //  <1>             1 if the CUESHEET corresponds to a Compact Disc, else 0.
//  //  <7+258*8>       Reserved. All bits must be set to zero.
//  //  <8>             The number of tracks. Must be at least 1 (because of the requisite lead-out track). For CD-DA, this number must be no more than 100 (99 regular tracks and one lead-out track).
//  //  CUESHEET_TRACK+ One or more tracks. A CUESHEET block is required to have a lead-out track; it is always the last track in the CUESHEET. For CD-DA, the lead-out track number must be 170 as specified by the Red Book, otherwise is must be 255.
//  //
//  def codec(length: Int) : Codec[Cuesheet] = {
//
//   limitedSizeBytes(length,
//      ("catalogNumber" | fixedSizeBytes(128, ascii)) ::
//        ("leadInSamplesCounr" | long(64)) ::
//        ("isCdCuesheet" | bool) ::
//        ("reserved" | constant(BitVector(7+258*8, 0))) ::
//        ("tracks" | vectorOfN(uint8, CuesheetTrack.codec)))
//
//  }.as[Cuesheet]
//
//}
