# flac4s

[![BSD 3 Clause License](https://img.shields.io/github/license/nigeleke/flac4s?style=plastic)](https://github.com/nigeleke/flac4s/blob/master/LICENSE)
[![Language](https://img.shields.io/badge/language-Scala-blue.svg?style=plastic)](https://www.scala-lang.org)
[![Build](https://img.shields.io/github/actions/workflow/status/nigeleke/flac4s/acceptance.yml?style=plastic)](https://github.com/nigeleke/flac4s/actions/workflows/acceptance.yml)
[![Coverage](https://img.shields.io/codecov/c/github/nigeleke/flac4s?style=plastic&token=9Z6VJKS0LK)](https://codecov.io/gh/nigeleke/flac4s)
![Version](https://img.shields.io/github/v/tag/nigeleke/flac4s?style=plastic)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=plastic&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

## Background

The aim of this project is to provide an editor for tags in a FLAC audio file.

This is driven by inconsistencies of tag usage by my music players.
I have FLAC files in my collection where some players show 'Unknown Artist' and others show the actual artist.
This depends on whether they use `AlbumArtist` or `Artist` tags from the file.

This editor will eventually display tag content and allow updates in order to normalise across a music collection.

Based on the [Free Lossless Audio Codec - draft-ietf-cellar-flac-07](https://datatracker.ietf.org/doc/draft-ietf-cellar-flac/) specification.

  * **Note**

    Subsequently found [mp3tag](https://www.mp3tag.de/en/), which has enabled my collection to be normalised.

## Documentation

* [Site](https://nigeleke.github.io/flac4s)
* [GitHub](https://github.com/nigeleke/flac4s)
* [API - Core](https://nigeleke.github.io/flac4s/core/api/index.html)
* [Coverage Report](https://nigeleke.github.io/flac4s/coverage/index.html)

## Dev-shell

* `> nix develop --impure`

## Approach

* flac decoder - metadata only

## Test files

* [IETF Flac Specification](https://github.com/ietf-wg-cellar/flac-specification/)
* [IETF Flac Test Files](https://github.com/ietf-wg-cellar/flac-test-files/)
* [sfiera Flac Test Files](https://github.com/sfiera/flac-test-files/)
