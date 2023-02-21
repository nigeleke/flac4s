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

import logger.Logger.*

import scodec.*
import scodec.bits.*
import scodec.codecs.*

case class Picture(pictureType: Long,
                   description: String,
                   width: Long,
                   height: Long,
                   colourDepth: Long,
                   coloursCount: Long,
                   dataSize: Long,
                   data: BitVector
) extends MetadataBlock.Data

object Picture:

  //  <32>  The picture type according to the ID3v2 APIC frame:
  //        0 - Other
  //        1 - 32x32 pixels 'file icon' (PNG only)
  //        2 - Other file icon
  //        3 - Cover (front)
  //        4 - Cover (back)
  //        5 - Leaflet page
  //        6 - Media (e.g. label side of CD)
  //        7 - Lead artist/lead performer/soloist
  //        8 - Artist/performer
  //        9 - Conductor
  //        10 - Band/Orchestra
  //        11 - Composer
  //        12 - Lyricist/text writer
  //        13 - Recording Location
  //        14 - During recording
  //        15 - During performance
  //        16 - Movie/video screen capture
  //        17 - A bright coloured fish
  //        18 - Illustration
  //        19 - Band/artist logotype
  //        20 - Publisher/Studio logotype
  //        Others are reserved and should not be used. There may only be one each of picture type 1 and 2 in a file.
  //  <32>	The length of the MIME type string in bytes.
  //  <n*8>	The MIME type string, in printable ASCII characters 0x20-0x7e. The MIME type may also be --> to signify that the data part is a URL of the picture instead of the picture data itself.
  //  <32>	The length of the description string in bytes.
  //  <n*8>	The description of the picture, in UTF-8.
  //  <32>	The width of the picture in pixels.
  //  <32>	The height of the picture in pixels.
  //  <32>	The color depth of the picture in bits-per-pixel.
  //  <32>	For indexed-color pictures (e.g. GIF), the number of colors used, or 0 for non-indexed pictures.
  //  <32>	The length of the picture data in bytes.
  //  <n*8>	The binary picture data.
  //
  private val pictureTypeCodec  = logField("pictureType")(uint32)
  private val descriptionCodec  = logField("description")(ascii32)
  private val widthCodec        = logField("width")(uint32)
  private val heightCodec       = logField("height")(uint32)
  private val colourDepthCodec  = logField("colourDepth")(uint32)
  private val coloursCountCodec = logField("coloursCount")(uint32)
  private val dataCodec         = logField("data")(uint32.flatZip { size => codecs.bits(size * 8) })

  def codec: Codec[Picture] = {
    pictureTypeCodec ::
      descriptionCodec ::
      widthCodec ::
      heightCodec ::
      colourDepthCodec ::
      coloursCountCodec ::
      dataCodec
  }.as[Picture]
