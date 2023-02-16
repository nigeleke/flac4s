val scala3Version = "3.2.2"

ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala3Version

val configVersion     = "1.4.2"
val scalacheckVersion = "3.2.11.0"
val scalatestVersion  = "3.2.11"
val scodecBitsVersion = "1.1.34"
val scodecCoreVersion = "2.2.0"
val slf4jVersion      = "1.7.36"

lazy val root = project
  .in(file("."))
  .settings(name := "flac")
  .aggregate(core)

//resolvers += "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"

lazy val core = project
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.scodec" %% "scodec-bits" % scodecBitsVersion,
      "org.scodec" %% "scodec-core" % scodecCoreVersion,
//      "org.scodec" %% "scodec-scalaz" % "1.4.1a",
//      "org.scodec" %% "scodec-stream" % "1.0.1",
//      "org.scalacheck" %% "scalacheck" % scalacheckVersion % "test",
      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    )
  )
