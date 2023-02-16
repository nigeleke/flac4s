package com.nigeleke.flac
package codecs
package frame

import scodec.Attempt.Successful
import scodec.*
import scodec.bits.*
import scodec.codecs.*

import scala.annotation.tailrec

case class SubframeHeader(codedSubframeType: Int, numberOfWastedBits: Int)

object SubframeHeader:

  //  <1>	  Zero bit padding, to prevent sync-fooling string of 1s
  //  <6>	  Subframe type:
  //        000000 : SUBFRAME_CONSTANT
  //        000001 : SUBFRAME_VERBATIM
  //        00001x : reserved
  //        0001xx : reserved
  //        001xxx : if(xxx <= 4) SUBFRAME_FIXED, xxx=order ; else reserved
  //        01xxxx : reserved
  //        1xxxxx : SUBFRAME_LPC, xxxxx=order-1
  //  <1+k>	'Wasted bits-per-sample' flag:
  //        0 : no wasted bits-per-sample in source subblock, k=0
  //        1 : k wasted bits-per-sample in source subblock, k-1 follows, unary coded; e.g. k=3 => 001 follows, k=7 => 0000001 follows.
  //
  private val paddingCodec           = log("padding")(constant(bin"0"))
  private val codedSubframeTypeCodec = log("codedSubframeType")(uint(6))

  val codec: Codec[SubframeHeader] = {
    paddingCodec ::
      codedSubframeTypeCodec ::
      wastedCodec
  }.dropUnits.as[SubframeHeader]
