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

import scodec.Attempt.Successful
import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class SeekTable(seekPoints: Vector[SeekPoint]) extends MetadataBlock.Data

object SeekTable:

  //  SEEKPOINT+	One or more seek points
  //
  def codec(using header: MetadataBlock.Header): Codec[SeekTable] = {
    new Codec[SeekTable]:

      val seekPointLength = 8 + 8 + 2

      override def decode(bits: BitVector): Attempt[DecodeResult[SeekTable]] =
        val pointsCount = header.length / seekPointLength
        val points      = vectorOfN(provide(pointsCount), SeekPoint.codec).decode(bits).require
        Successful(DecodeResult(SeekTable(points.value), points.remainder))

      override def encode(value: SeekTable): Attempt[BitVector] = ???

      override def sizeBound: SizeBound = SizeBound.exact(header.length / seekPointLength)
  }
