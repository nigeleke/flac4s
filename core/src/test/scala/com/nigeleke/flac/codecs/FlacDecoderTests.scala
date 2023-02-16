package com.nigeleke.flac.codecs

import org.scalatest.*
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.*
import scodec.Attempt.*

class FlacDecoderTests extends AnyWordSpec with Matchers:

  private def doDecode(resourceFile: String) =
    val path        = "/com/nigeleke/flac/" + resourceFile
    val stream      = getClass().getResourceAsStream(path)
    val attemptFlac = Flac.from(stream)
    attemptFlac match
      case Successful(flac) => flac.remainder should be(empty)
      case Failure(error)   => fail(error.toString)

  "FlacDecoder" should {
    "successfully decode IETF example flac files" when {
      "example_1.flac" in { doDecode("ietf/examples/example_1.flac") }
      "example_2.flac" in { doDecode("ietf/examples/example_2.flac") }
      "example_3.flac" in { doDecode("ietf/examples/example_3.flac") }
    }

    "succesfully decode ietf-wg-cellar/flac-test-files" when {
      "01 - blocksize 4096.flac" in { doDecode("ietf/cellar/01 - blocksize 4096.flac") }
      "02 - blocksize 4608.flac" in { doDecode("ietf/cellar/02 - blocksize 4608.flac") }
      "03 - blocksize 16.flac" in { doDecode("ietf/cellar/03 - blocksize 16.flac") }
      "04 - blocksize 192.flac" in { doDecode("ietf/cellar/04 - blocksize 192.flac") }
      "05 - blocksize 254.flac" in { doDecode("ietf/cellar/05 - blocksize 254.flac") }
      "06 - blocksize 512.flac" in { doDecode("ietf/cellar/06 - blocksize 512.flac") }
      "07 - blocksize 725.flac" in { doDecode("ietf/cellar/07 - blocksize 725.flac") }
      "08 - blocksize 1000.flac" in { doDecode("ietf/cellar/08 - blocksize 1000.flac") }
      "09 - blocksize 1937.flac" in { doDecode("ietf/cellar/09 - blocksize 1937.flac") }
      "10 - blocksize 2304.flac" in { doDecode("ietf/cellar/10 - blocksize 2304.flac") }
    }

    "successfully decode github.com/sfiera/flac-test-files" when {
      "stereo.flac" in { doDecode("sfiera/stereo.flac") }
      "center.flac" in { doDecode("sfiera/center.flac") }
      "quad.flac" in { doDecode("sfiera/quad.flac") }
      "surround50.flac" in { doDecode("sfiera/surround50.flac") }
      "surround51.flac" in { doDecode("sfiera/surround51.flac") }
      "surround61.flac" in { doDecode("sfiera/surround61.flac") }
      "surround71.flac" in { doDecode("sfiera/surround71.flac") }
    }
  }
