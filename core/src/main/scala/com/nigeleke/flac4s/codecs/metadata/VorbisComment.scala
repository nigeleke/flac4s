/*
 * Copyright (c) 2023, Nigel Eke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.nigeleke.flac4s
package codecs
package metadata

import logger.Logger.logField

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

  // TODO: Flac spec states should be unsigned; scodec documentation states signed
  private val vendorCodec = logField("vendor")(utf8_32L)

  // TODO: Flac spec states should be unsigned; scodec documentation states signed
  private val commentCodec = logField("comment")(utf8_32L)

  // TODO: Flac spec states unsigned long. Can't hold this in Vector.
  private val commentsCodec = vectorOfN(int32L, commentCodec)

  val codec: Codec[VorbisComment] = {
    (vendorCodec :: commentsCodec)
  }.dropUnits.as[VorbisComment]
