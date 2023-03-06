package com.nigeleke.flac4s

import codecs.*

import org.scalatest.*
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.*

import scodec.Attempt.*
import scodec.bits.BitVector

class FlacDecoderTests extends AnyWordSpec with Matchers:

  private def attemptDecode(resourceFile: String) =
    val path   = "/com/nigeleke/flac4s/" + resourceFile
    val stream = getClass().getResourceAsStream(path)
    Flac.from(stream)

  private def doDecode(resourceFile: String) =
    attemptDecode(resourceFile) match
      case Successful(flacResult) => flacResult.remainder should be(empty)
      case Failure(error)         => fail(error.toString)

  private def dontDecode(resourceFile: String) =
    attemptDecode(resourceFile) match
      case Successful(flac) => fail("unexpected successful decode: $flac")
      case Failure(error)   => succeed

  private def doRoundTrip(resourceFile: String) =
    val path            = "/com/nigeleke/flac4s/" + resourceFile
    val stream          = getClass().getResourceAsStream(path)
    val bitVectorSource = BitVector(stream.readAllBytes())
    Flac.from(bitVectorSource) match
      case Successful(flacResult) =>
        flacResult.value.encode match
          case Successful(bitVectorTarget) => bitVectorTarget should be(bitVectorSource)
          case Failure(error)              => fail(error.toString)
      case Failure(error) => fail(error.toString)

  "FlacDecoder" should {
    "successfully decode ietf-wg-cellar/flac-specificition example files" when {
      "example_1.flac" in { doDecode("flac-test-files/examples/example_1.flac") }
      "example_2.flac" in { doDecode("flac-test-files/examples/example_2.flac") }
      "example_3.flac" in { doDecode("flac-test-files/examples/example_3.flac") }
    }

    "succesfully decode ietf-wg-cellar/flac-test-files" when {
      // format: off
      "01 - blocksize 4096.flac" in { doDecode("flac-test-files/subset/01 - blocksize 4096.flac") }
      "02 - blocksize 4608.flac" in { doDecode("flac-test-files/subset/02 - blocksize 4608.flac") }
      "03 - blocksize 16.flac" in { doDecode("flac-test-files/subset/03 - blocksize 16.flac") }
      "04 - blocksize 192.flac" in { doDecode("flac-test-files/subset/04 - blocksize 192.flac") }
      "05 - blocksize 254.flac" in { doDecode("flac-test-files/subset/05 - blocksize 254.flac") }
      "06 - blocksize 512.flac" in { doDecode("flac-test-files/subset/06 - blocksize 512.flac") }
      "07 - blocksize 725.flac" in { doDecode("flac-test-files/subset/07 - blocksize 725.flac") }
      "08 - blocksize 1000.flac" in { doDecode("flac-test-files/subset/08 - blocksize 1000.flac") }
      "09 - blocksize 1937.flac" in { doDecode("flac-test-files/subset/09 - blocksize 1937.flac") }
      "10 - blocksize 2304.flac" in { doDecode("flac-test-files/subset/10 - blocksize 2304.flac") }
      "11 - partition order 8.flac" in { doDecode("flac-test-files/subset/11 - partition order 8.flac") }
      "12 - qlp precision 15 bit.flac" in { doDecode("flac-test-files/subset/12 - qlp precision 15 bit.flac") }
      "13 - qlp precision 2 bit.flac" in { doDecode("flac-test-files/subset/13 - qlp precision 2 bit.flac") }
      "14 - wasted bits.flac" in { doDecode("flac-test-files/subset/14 - wasted bits.flac") }
      "15 - only verbatim subframes.flac" in { doDecode("flac-test-files/subset/15 - only verbatim subframes.flac") }
      "16 - partition order 8 containing escaped partitions.flac" in { doDecode("flac-test-files/subset/16 - partition order 8 containing escaped partitions.flac") }
      "17 - all fixed orders.flac" in {doDecode("flac-test-files/subset/17 - all fixed orders.flac") }
      "18 - precision search.flac" in { doDecode("flac-test-files/subset/18 - precision search.flac") }
      "19 - samplerate 35467Hz.flac" in { doDecode("flac-test-files/subset/19 - samplerate 35467Hz.flac") }
      "20 - samplerate 39kHz.flac" in { doDecode("flac-test-files/subset/20 - samplerate 39kHz.flac") }
      "21 - samplerate 22050Hz.flac" in { doDecode("flac-test-files/subset/21 - samplerate 22050Hz.flac") }
      "22 - 12 bit per sample.flac" in { doDecode("flac-test-files/subset/22 - 12 bit per sample.flac") }
      "23 - 8 bit per sample.flac" in { doDecode("flac-test-files/subset/23 - 8 bit per sample.flac") }
      "24 - variable blocksize file created with flake revision 264.flac" in { doDecode("flac-test-files/subset/24 - variable blocksize file created with flake revision 264.flac") }
      "25 - variable blocksize file created with flake revision 264, modified to create smaller blocks.flac" in { doDecode("flac-test-files/subset/25 - variable blocksize file created with flake revision 264, modified to create smaller blocks.flac") }
      "26 - variable blocksize file created with CUETools.Flake 2.1.6.flac" in { doDecode("flac-test-files/subset/26 - variable blocksize file created with CUETools.Flake 2.1.6.flac") }
      "27 - old format variable blocksize file created with Flake 0.11.flac" in { doDecode("flac-test-files/subset/27 - old format variable blocksize file created with Flake 0.11.flac") }
      "28 - high resolution audio, default settings.flac" in { doDecode("flac-test-files/subset/28 - high resolution audio, default settings.flac") }
      "29 - high resolution audio, blocksize 16384.flac" in { doDecode("flac-test-files/subset/29 - high resolution audio, blocksize 16384.flac") }
      "30 - high resolution audio, blocksize 13456.flac" in { doDecode("flac-test-files/subset/30 - high resolution audio, blocksize 13456.flac") }
      "31 - high resolution audio, using only 32nd order predictors.flac" in { doDecode("flac-test-files/subset/31 - high resolution audio, using only 32nd order predictors.flac") }
      "32 - high resolution audio, partition order 8 containing escaped partitions.flac" in { doDecode("flac-test-files/subset/32 - high resolution audio, partition order 8 containing escaped partitions.flac") }
      "33 - samplerate 192kHz.flac" in { doDecode("flac-test-files/subset/33 - samplerate 192kHz.flac") }
      "34 - samplerate 192kHz, using only 32nd order predictors.flac" in { doDecode("flac-test-files/subset/34 - samplerate 192kHz, using only 32nd order predictors.flac") }
      "35 - samplerate 134560Hz.flac" in { doDecode("flac-test-files/subset/35 - samplerate 134560Hz.flac") }
      "36 - samplerate 384kHz.flac" in { doDecode("flac-test-files/subset/36 - samplerate 384kHz.flac") }
      "37 - 20 bit per sample.flac" in { doDecode("flac-test-files/subset/37 - 20 bit per sample.flac") }
      "38 - 3 channels (3.0).flac" in { doDecode("flac-test-files/subset/38 - 3 channels (3.0).flac") }
      "39 - 4 channels (4.0).flac" in { doDecode("flac-test-files/subset/39 - 4 channels (4.0).flac") }
      "40 - 5 channels (5.0).flac" in { doDecode("flac-test-files/subset/40 - 5 channels (5.0).flac") }
      "41 - 6 channels (5.1).flac" in { doDecode("flac-test-files/subset/41 - 6 channels (5.1).flac") }
      "42 - 7 channels (6.1).flac" in { doDecode("flac-test-files/subset/42 - 7 channels (6.1).flac") }
      "43 - 8 channels (7.1).flac" in { doDecode("flac-test-files/subset/43 - 8 channels (7.1).flac") }
      "44 - 8-channel surround, 192kHz, 24 bit, using only 32nd order predictors.flac" in { doDecode("flac-test-files/subset/44 - 8-channel surround, 192kHz, 24 bit, using only 32nd order predictors.flac") }
      "45 - no total number of samples set.flac" in { doDecode("flac-test-files/subset/45 - no total number of samples set.flac") }
      "46 - no min-max framesize set.flac" in { doDecode("flac-test-files/subset/46 - no min-max framesize set.flac") }
      "47 - only STREAMINFO.flac" in { doDecode("flac-test-files/subset/47 - only STREAMINFO.flac") }
      "48 - Extremely large SEEKTABLE.flac" in { doDecode("flac-test-files/subset/48 - Extremely large SEEKTABLE.flac") }
      "49 - Extremely large PADDING.flac" in { doDecode("flac-test-files/subset/49 - Extremely large PADDING.flac") }
      "50 - Extremely large PICTURE.flac" in { doDecode("flac-test-files/subset/50 - Extremely large PICTURE.flac") }
      "51 - Extremely large VORBISCOMMENT.flac" in { doDecode("flac-test-files/subset/51 - Extremely large VORBISCOMMENT.flac") }
      "52 - Extremely large APPLICATION.flac" in { doDecode("flac-test-files/subset/52 - Extremely large APPLICATION.flac") }
      "53 - CUESHEET with very many indexes.flac" in { doDecode("flac-test-files/subset/53 - CUESHEET with very many indexes.flac") }
      "54 - 1000x repeating VORBISCOMMENT.flac" in { doDecode("flac-test-files/subset/54 - 1000x repeating VORBISCOMMENT.flac") }
      "55 - file 48-53 combined.flac" ignore { doDecode("flac-test-files/subset/55 - file 48-53 combined.flac") }
      "56 - JPG PICTURE.flac" in { doDecode("flac-test-files/subset/56 - JPG PICTURE.flac") }
      "57 - PNG PICTURE.flac" in { doDecode("flac-test-files/subset/57 - PNG PICTURE.flac") }
      "58 - GIF PICTURE.flac" in { doDecode("flac-test-files/subset/58 - GIF PICTURE.flac") }
      "59 - AVIF PICTURE.flac" in { doDecode("flac-test-files/subset/59 - AVIF PICTURE.flac") }
      "60 - mono audio.flac" in { doDecode("flac-test-files/subset/60 - mono audio.flac") }
      "61 - predictor overflow check, 16-bit.flac" in { doDecode("flac-test-files/subset/61 - predictor overflow check, 16-bit.flac") }
      "62 - predictor overflow check, 20-bit.flac" in { doDecode("flac-test-files/subset/62 - predictor overflow check, 20-bit.flac") }
      "63 - predictor overflow check, 24-bit.flac" in { doDecode("flac-test-files/subset/63 - predictor overflow check, 24-bit.flac") }
      "64 - rice partitions with escape code zero.flac" in { doDecode("flac-test-files/subset/64 - rice partitions with escape code zero.flac") }
      "01 - changing samplerate.flac" in { doDecode("flac-test-files/uncommon/01 - changing samplerate.flac") }
      "02 - increasing number of channels.flac" in { doDecode("flac-test-files/uncommon/02 - increasing number of channels.flac") }
      "03 - decreasing number of channels.flac" in { doDecode("flac-test-files/uncommon/03 - decreasing number of channels.flac") }
      "04 - changing bitdepth.flac" in { doDecode("flac-test-files/uncommon/04 - changing bitdepth.flac") }
      "05 - 32bps audio.flac" in { doDecode("flac-test-files/uncommon/05 - 32bps audio.flac") }
      "06 - samplerate 768kHz.flac" in { doDecode("flac-test-files/uncommon/06 - samplerate 768kHz.flac") }
      "07 - 15 bit per sample.flac" in { doDecode("flac-test-files/uncommon/07 - 15 bit per sample.flac") }
      "08 - blocksize 65535.flac" in { doDecode("flac-test-files/uncommon/08 - blocksize 65535.flac") }
      "09 - Rice partition order 15.flac" in { doDecode("flac-test-files/uncommon/09 - Rice partition order 15.flac") }
      // format: on
    }

    "not worry about multicast ietf-wg-cellar/flac-test-files" when {
      // format: off
      "10 - file starting at frame header.flac" in { dontDecode("flac-test-files/uncommon/10 - file starting at frame header.flac") }
      "11 - file starting with unparsable data.flac" in { dontDecode("flac-test-files/uncommon/11 - file starting with unparsable data.flac") }
      // format: on
    }

    "successfully decode sfiera/flac-test-files" when {
      "stereo.flac" in { doDecode("sfiera/stereo.flac") }
      "center.flac" in { doDecode("sfiera/center.flac") }
      "quad.flac" in { doDecode("sfiera/quad.flac") }
      "surround50.flac" in { doDecode("sfiera/surround50.flac") }
      "surround51.flac" in { doDecode("sfiera/surround51.flac") }
      "surround61.flac" in { doDecode("sfiera/surround61.flac") }
      "surround71.flac" in { doDecode("sfiera/surround71.flac") }
    }

    "successfully round trip a decode - encode - decode sequence" when {
      // format: off
      "example_1.flac" in { doRoundTrip("flac-test-files/examples/example_1.flac") }
      "example_2.flac" in { doRoundTrip("flac-test-files/examples/example_2.flac") }
      "example_3.flac" in { doRoundTrip("flac-test-files/examples/example_3.flac") }
      "01 - blocksize 4096.flac" in { doRoundTrip("flac-test-files/subset/01 - blocksize 4096.flac") }
      "02 - blocksize 4608.flac" in { doRoundTrip("flac-test-files/subset/02 - blocksize 4608.flac") }
      "03 - blocksize 16.flac" in { doRoundTrip("flac-test-files/subset/03 - blocksize 16.flac") }
      "04 - blocksize 192.flac" in { doRoundTrip("flac-test-files/subset/04 - blocksize 192.flac") }
      "05 - blocksize 254.flac" in { doRoundTrip("flac-test-files/subset/05 - blocksize 254.flac") }
      "06 - blocksize 512.flac" in { doRoundTrip("flac-test-files/subset/06 - blocksize 512.flac") }
      "07 - blocksize 725.flac" in { doRoundTrip("flac-test-files/subset/07 - blocksize 725.flac") }
      "08 - blocksize 1000.flac" in { doRoundTrip("flac-test-files/subset/08 - blocksize 1000.flac") }
      "09 - blocksize 1937.flac" in { doRoundTrip("flac-test-files/subset/09 - blocksize 1937.flac") }
      "10 - blocksize 2304.flac" in { doRoundTrip("flac-test-files/subset/10 - blocksize 2304.flac") }
      "11 - partition order 8.flac" in { doRoundTrip("flac-test-files/subset/11 - partition order 8.flac") }
      "12 - qlp precision 15 bit.flac" in { doRoundTrip("flac-test-files/subset/12 - qlp precision 15 bit.flac") }
      "13 - qlp precision 2 bit.flac" in { doRoundTrip("flac-test-files/subset/13 - qlp precision 2 bit.flac") }
      "14 - wasted bits.flac" in { doRoundTrip("flac-test-files/subset/14 - wasted bits.flac") }
      "15 - only verbatim subframes.flac" in { doRoundTrip("flac-test-files/subset/15 - only verbatim subframes.flac") }
      "16 - partition order 8 containing escaped partitions.flac" in { doRoundTrip("flac-test-files/subset/16 - partition order 8 containing escaped partitions.flac") }
      "17 - all fixed orders.flac" in {doRoundTrip("flac-test-files/subset/17 - all fixed orders.flac") }
      "18 - precision search.flac" in { doRoundTrip("flac-test-files/subset/18 - precision search.flac") }
      "19 - samplerate 35467Hz.flac" in { doRoundTrip("flac-test-files/subset/19 - samplerate 35467Hz.flac") }
      "20 - samplerate 39kHz.flac" in { doRoundTrip("flac-test-files/subset/20 - samplerate 39kHz.flac") }
      "21 - samplerate 22050Hz.flac" in { doRoundTrip("flac-test-files/subset/21 - samplerate 22050Hz.flac") }
      "22 - 12 bit per sample.flac" in { doRoundTrip("flac-test-files/subset/22 - 12 bit per sample.flac") }
      "23 - 8 bit per sample.flac" in { doRoundTrip("flac-test-files/subset/23 - 8 bit per sample.flac") }
      "24 - variable blocksize file created with flake revision 264.flac" in { doRoundTrip("flac-test-files/subset/24 - variable blocksize file created with flake revision 264.flac") }
      "25 - variable blocksize file created with flake revision 264, modified to create smaller blocks.flac" in { doRoundTrip("flac-test-files/subset/25 - variable blocksize file created with flake revision 264, modified to create smaller blocks.flac") }
      "26 - variable blocksize file created with CUETools.Flake 2.1.6.flac" in { doRoundTrip("flac-test-files/subset/26 - variable blocksize file created with CUETools.Flake 2.1.6.flac") }
      "27 - old format variable blocksize file created with Flake 0.11.flac" in { doRoundTrip("flac-test-files/subset/27 - old format variable blocksize file created with Flake 0.11.flac") }
      "28 - high resolution audio, default settings.flac" in { doRoundTrip("flac-test-files/subset/28 - high resolution audio, default settings.flac") }
      "29 - high resolution audio, blocksize 16384.flac" in { doRoundTrip("flac-test-files/subset/29 - high resolution audio, blocksize 16384.flac") }
      "30 - high resolution audio, blocksize 13456.flac" in { doRoundTrip("flac-test-files/subset/30 - high resolution audio, blocksize 13456.flac") }
      "31 - high resolution audio, using only 32nd order predictors.flac" in { doRoundTrip("flac-test-files/subset/31 - high resolution audio, using only 32nd order predictors.flac") }
      "32 - high resolution audio, partition order 8 containing escaped partitions.flac" in { doRoundTrip("flac-test-files/subset/32 - high resolution audio, partition order 8 containing escaped partitions.flac") }
      "33 - samplerate 192kHz.flac" in { doRoundTrip("flac-test-files/subset/33 - samplerate 192kHz.flac") }
      "34 - samplerate 192kHz, using only 32nd order predictors.flac" in { doRoundTrip("flac-test-files/subset/34 - samplerate 192kHz, using only 32nd order predictors.flac") }
      "35 - samplerate 134560Hz.flac" in { doRoundTrip("flac-test-files/subset/35 - samplerate 134560Hz.flac") }
      "36 - samplerate 384kHz.flac" in { doRoundTrip("flac-test-files/subset/36 - samplerate 384kHz.flac") }
      "37 - 20 bit per sample.flac" in { doRoundTrip("flac-test-files/subset/37 - 20 bit per sample.flac") }
      "38 - 3 channels (3.0).flac" in { doRoundTrip("flac-test-files/subset/38 - 3 channels (3.0).flac") }
      "39 - 4 channels (4.0).flac" in { doRoundTrip("flac-test-files/subset/39 - 4 channels (4.0).flac") }
      "40 - 5 channels (5.0).flac" in { doRoundTrip("flac-test-files/subset/40 - 5 channels (5.0).flac") }
      "41 - 6 channels (5.1).flac" in { doRoundTrip("flac-test-files/subset/41 - 6 channels (5.1).flac") }
      "42 - 7 channels (6.1).flac" in { doRoundTrip("flac-test-files/subset/42 - 7 channels (6.1).flac") }
      "43 - 8 channels (7.1).flac" in { doRoundTrip("flac-test-files/subset/43 - 8 channels (7.1).flac") }
      "44 - 8-channel surround, 192kHz, 24 bit, using only 32nd order predictors.flac" in { doRoundTrip("flac-test-files/subset/44 - 8-channel surround, 192kHz, 24 bit, using only 32nd order predictors.flac") }
      "45 - no total number of samples set.flac" in { doRoundTrip("flac-test-files/subset/45 - no total number of samples set.flac") }
      "46 - no min-max framesize set.flac" in { doRoundTrip("flac-test-files/subset/46 - no min-max framesize set.flac") }
      "47 - only STREAMINFO.flac" in { doRoundTrip("flac-test-files/subset/47 - only STREAMINFO.flac") }
      "48 - Extremely large SEEKTABLE.flac" in { doRoundTrip("flac-test-files/subset/48 - Extremely large SEEKTABLE.flac") }
      "49 - Extremely large PADDING.flac" in { doRoundTrip("flac-test-files/subset/49 - Extremely large PADDING.flac") }
      "50 - Extremely large PICTURE.flac" in { doRoundTrip("flac-test-files/subset/50 - Extremely large PICTURE.flac") }
      "51 - Extremely large VORBISCOMMENT.flac" in { doRoundTrip("flac-test-files/subset/51 - Extremely large VORBISCOMMENT.flac") }
      "52 - Extremely large APPLICATION.flac" in { doRoundTrip("flac-test-files/subset/52 - Extremely large APPLICATION.flac") }
      "53 - CUESHEET with very many indexes.flac" in { doRoundTrip("flac-test-files/subset/53 - CUESHEET with very many indexes.flac") }
      "54 - 1000x repeating VORBISCOMMENT.flac" in { doRoundTrip("flac-test-files/subset/54 - 1000x repeating VORBISCOMMENT.flac") }
      "55 - file 48-53 combined.flac" ignore { doRoundTrip("flac-test-files/subset/55 - file 48-53 combined.flac") }
      "56 - JPG PICTURE.flac" in { doRoundTrip("flac-test-files/subset/56 - JPG PICTURE.flac") }
      "57 - PNG PICTURE.flac" in { doRoundTrip("flac-test-files/subset/57 - PNG PICTURE.flac") }
      "58 - GIF PICTURE.flac" in { doRoundTrip("flac-test-files/subset/58 - GIF PICTURE.flac") }
      "59 - AVIF PICTURE.flac" in { doRoundTrip("flac-test-files/subset/59 - AVIF PICTURE.flac") }
      "60 - mono audio.flac" in { doRoundTrip("flac-test-files/subset/60 - mono audio.flac") }
      "61 - predictor overflow check, 16-bit.flac" in { doRoundTrip("flac-test-files/subset/61 - predictor overflow check, 16-bit.flac") }
      "62 - predictor overflow check, 20-bit.flac" in { doRoundTrip("flac-test-files/subset/62 - predictor overflow check, 20-bit.flac") }
      "63 - predictor overflow check, 24-bit.flac" in { doRoundTrip("flac-test-files/subset/63 - predictor overflow check, 24-bit.flac") }
      "64 - rice partitions with escape code zero.flac" in { doRoundTrip("flac-test-files/subset/64 - rice partitions with escape code zero.flac") }
      "01 - changing samplerate.flac" in { doRoundTrip("flac-test-files/uncommon/01 - changing samplerate.flac") }
      "02 - increasing number of channels.flac" in { doRoundTrip("flac-test-files/uncommon/02 - increasing number of channels.flac") }
      "03 - decreasing number of channels.flac" in { doRoundTrip("flac-test-files/uncommon/03 - decreasing number of channels.flac") }
      "04 - changing bitdepth.flac" in { doRoundTrip("flac-test-files/uncommon/04 - changing bitdepth.flac") }
      "05 - 32bps audio.flac" in { doRoundTrip("flac-test-files/uncommon/05 - 32bps audio.flac") }
      "06 - samplerate 768kHz.flac" in { doRoundTrip("flac-test-files/uncommon/06 - samplerate 768kHz.flac") }
      "07 - 15 bit per sample.flac" in { doRoundTrip("flac-test-files/uncommon/07 - 15 bit per sample.flac") }
      "08 - blocksize 65535.flac" in { doRoundTrip("flac-test-files/uncommon/08 - blocksize 65535.flac") }
      "09 - Rice partition order 15.flac" in { doRoundTrip("flac-test-files/uncommon/09 - Rice partition order 15.flac") }
      "stereo.flac" in { doRoundTrip("sfiera/stereo.flac") }
      "center.flac" in { doRoundTrip("sfiera/center.flac") }
      "quad.flac" in { doRoundTrip("sfiera/quad.flac") }
      "surround50.flac" in { doRoundTrip("sfiera/surround50.flac") }
      "surround51.flac" in { doRoundTrip("sfiera/surround51.flac") }
      "surround61.flac" in { doRoundTrip("sfiera/surround61.flac") }
      "surround71.flac" in { doRoundTrip("sfiera/surround71.flac") }
      // format: on
    }
  }
