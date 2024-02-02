import Dependencies._
import xerial.sbt.Sonatype.autoImport.sonatypeRepository

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "io.github"
ThisBuild / organizationName := "edefritz"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
Test / parallelExecution := false

// library name
ThisBuild / name := "scala-tile38"

// library version
ThisBuild / version := "0.2"

// groupId, SCM, license information
ThisBuild / organization := "io.github.edefritz"
ThisBuild / homepage := Some(url("https://github.com/Edefritz/scala-tile38"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/Edefritz/scala-tile38"),
    "git@github.com:edefritz/scala-tile38.git"
  )
)
ThisBuild / developers := List(
  Developer(
    "eduard",
    "eduard",
    "edefritz@gmail.com",
    url("https://gitlab.com/edefritz")
  )
)
ThisBuild / licenses += ("MIT", url("https://opensource.org/licenses/MIT"))
ThisBuild / publishMavenStyle := true

// disable publishw ith scala version, otherwise artifact name will include scala version
ThisBuild / crossPaths := false

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

lazy val root = (project in file("."))
  .settings(
    name := "scala-tile38",
    libraryDependencies ++= projectDeps
  )
