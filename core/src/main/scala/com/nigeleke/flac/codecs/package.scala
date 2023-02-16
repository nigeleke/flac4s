package com.nigeleke.flac

import scodec.*
import scodec.codecs.*

package object codecs:

  def log[A](name: String)(codec: Codec[A]) = logToStdOut((name | codec), name)
//  inline def log[A](name: String)(codec: Codec[A]) = (name | codec)
