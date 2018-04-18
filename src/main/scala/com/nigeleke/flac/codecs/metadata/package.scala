package com.nigeleke.flac.codecs

package object metadata {

  //  METADATA_BLOCK_STREAMINFO
  //    ...
  //    <3>	(number of channels)-1. FLAC supports from 1 to 8 channels
  //    ...
  //
  def numberOfChannels(implicit streamInfo: StreamInfo) : Int = streamInfo.channelsCount + 1

}
