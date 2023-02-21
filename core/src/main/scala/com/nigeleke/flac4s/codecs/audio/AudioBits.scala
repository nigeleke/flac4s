package com.nigeleke.flac
package codecs
package audio

import scodec.*
import scodec.bits.*
import scodec.codecs.*

final case class AudioBits(bits: BitVector)

object AudioBits:
  def codec = codecs.bits.as[AudioBits]
