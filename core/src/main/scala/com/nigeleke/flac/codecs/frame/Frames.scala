package com.nigeleke.flac
package codecs
package frame

import metadata.*

import scodec.*
import scodec.codecs.*

type Frames = List[Frame]

object Frames:
  def codec(using streamInfo: StreamInfo): Codec[Frames] =
    log("frames")(list(Frame.codec))
