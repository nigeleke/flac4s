package com.nigeleke.flac.codecs

import org.scalatest.{FunSuite, Matchers, OptionValues}
import org.scalatest.prop.TableDrivenPropertyChecks

class FlacDecoderTests extends FunSuite with TableDrivenPropertyChecks with Matchers with OptionValues {

  val flacFiles = Table("filename",
    "01_stereo.flac", "02_center.flac", "03_quad.flac",
    "04_surround50.flac", "05_surround51.flac", "06_surround61.flac", "07_surround71.flac")

  test("FlacDecoder will successfully decode FlacApp files") {
    forAll(flacFiles) { filename =>
      val path = "/com/nigeleke/flac/" + filename
      val stream = getClass().getResourceAsStream(path)
      val mayBeFlac = Flac.from(stream)
      mayBeFlac.isDefined should be (true)
    }
  }

}
