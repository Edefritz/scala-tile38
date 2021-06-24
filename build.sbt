import Dependencies._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"
Test / parallelExecution := false

lazy val root = (project in file("."))
  .settings(
    name := "scala-tile38",
    libraryDependencies ++= projectDeps
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
