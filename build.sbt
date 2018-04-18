name := "flac"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"

libraryDependencies ++= Seq(

  "org.scodec" %% "scodec-bits" % "1.1.5",
  "org.scodec" %% "scodec-core" % "1.10.3",
  "org.scodec" %% "scodec-scalaz" % "1.4.1a",
  "org.scodec" %% "scodec-stream" % "1.0.1",

//  "org.scalactic" %% "scalactic" % "3.0.4",

  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)