# flac

The aim of this project is to provide an editor for tags in a FLAC audio file.

This is driven by inconsistencies of tag usage my music players. I have FLAC files in my collection where some players show 'Unknown Artist' and others show the actual artist. This (to be proven) seems to depend on whether they use `AlbumArtist` or `Artist` tags from the file.

This editor will display tag content and allow updates in order to normalise across a music collection.

## Step one - flac decoder

Written in [Scala 3](https://scala-lang.org) using the [scodec](https://scodec.org/) library.

Based on the [Free Lossless Audio Codec - draft-ietf-cellar-flac-07](https://datatracker.ietf.org/doc/draft-ietf-cellar-flac/) specification.

### Test files

| File      | Source                                                                                  | Licence                                                                                      |
|-----------|-----------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| example_1.flac | [IETF](https://github.com/ietf-wg-cellar/flac-specification/blob/master/example_1.flac) | [BSD-3-Clause](https://github.com/ietf-wg-cellar/flac-specification/blob/master/LICENSE.txt) |
| example_2.flac | [IETF](https://github.com/ietf-wg-cellar/flac-specification/blob/master/example_2.flac) | "                                                                                            |
| example_3.flac | [IETF](https://github.com/ietf-wg-cellar/flac-specification/blob/master/example_3.flac) | "                                                                                            |
| stereo.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/stereo.flac)             | [GPL-2.0](https://github.com/sfiera/flac-test-files/blob/master/COPYING)                     |
| center.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/center.flac)             | "                                                                                            |
| quad.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/quad.flac)               | "                                                                                             |
| surround50.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/surround50.flac)         | "                                                                                             |
| surround51.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/surround51.flac)         | "                                                                                             |
| surround61.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/surround61.flac)         | "                                                                                             |
| surround71.flac | [sfiera](https://github.com/sfiera/flac-test-files/blob/master/surround71.flac)         | "                                                                                             |
 | | [misc](https://github.com/ietf-wg-cellar/flac-test-files)                               |