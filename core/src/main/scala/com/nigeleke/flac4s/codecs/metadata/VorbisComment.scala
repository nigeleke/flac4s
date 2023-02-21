package com.nigeleke.flac
package codecs
package metadata

import scodec.*
import scodec.bits.*
import scodec.codecs.*

final case class VorbisComment(vendor: String, comments: Vector[String]) extends MetadataBlock.Data

object VorbisComment:

  //  The comment header is decoded as follows:
  //
  //  1) [vendor_length] = read an unsigned integer of 32 bits
  //  2) [vendor_string] = read a UTF-8 vector as [vendor_length] octets
  //  3) [user_comment_list_length] = read an unsigned integer of 32 bits
  //  4) iterate [user_comment_list_length] times {
  //      5) [length] = read an unsigned integer of 32 bits
  //      6) this iteration's user comment = read a UTF-8 vector as [length] octets
  //  7) [framing_bit] = read a single bit as boolean
  //  8) -- FlacApp spec states "without the framing bit" if ( [framing_bit] unset or end of packet ) then ERROR
  //  9) done.
  //
  private val vendorCodec =
    utf8_32L // TODO: Flac spec states should be unsigned; scodec documentation states signed

  private val commentCodec =
    utf8_32L // TODO: Flac spec states should be unsigned; scodec documentation states signed

  private val commentsCodec =
    vectorOfN(int32L, commentCodec) // TODO: Flac spec states unsigned long. Can't hold in Vector.

  val codec: Codec[VorbisComment] = {
    (vendorCodec :: commentsCodec)
  }.dropUnits.as[VorbisComment]
