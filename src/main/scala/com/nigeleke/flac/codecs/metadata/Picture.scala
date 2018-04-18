package com.nigeleke.flac.codecs.metadata

import scodec._
import scodec.codecs._

case class Picture(pictureType: Int,
                   description: String,
                   width: Int,
                   height: Int,
                   colourDepth: Int,
                   coloursCount: Int,
                   data: Vector[Byte]) extends MetadataBlock

object Picture {

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
  def codec : Codec[Picture] = {
    ("pictureType" | int32) ::
      ("description" | ascii32) ::
      ("width" | int32) ::
      ("height" | int32) ::
      ("colourDepth" | int32) ::
      ("coloursCount" | int32) ::
      ("data" | vectorOfN(int32, byte))
  }.as[Picture]

}