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

import scodec.*
import scodec.bits.*
import scodec.codecs.*

import scala.annotation.tailrec

type MetadataBlocks = List[MetadataBlock]

object MetadataBlocks:
  //  <1>   Last-metadata-block flag: '1' if this block is the last metadata block before the audio blocks, '0' otherwise.
  //  <7>	  BLOCK_TYPE
  //        0 : STREAMINFO
  //        1 : PADDING
  //        2 : APPLICATION
  //        3 : SEEKTABLE
  //        4 : VORBIS_COMMENT
  //        5 : CUESHEET
  //        6 : PICTURE
  //        7-126 : reserved
  //        127 : invalid, to avoid confusion with a frame sync code
  //  <24>	Length (in bytes) of metadata to follow (does not include the size of the METADATA_BLOCK_HEADER)
  //
  // List[MetadataBlock] is NonEmpty as StreamInfo is a mandatory first block.
  //
  val codec: Codec[MetadataBlocks] = 
    MetadataBlock.codec.consume(block =>
      if block.header.isLast
      then provide(List(block))
      else codec.xmap(blocks => block +: blocks, blocks => blocks.tail)
    )(blocks => blocks.head)
