import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2"
  val lettuce = "io.lettuce" % "lettuce-core" % "5.0.2.RELEASE"

  val circeVersion = "0.14.1"
  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-shapes"
  ).map(_ % circeVersion)

  val scalaMock = Seq(
    "org.scalamock" %% "scalamock" % "5.1.0" % Test,
    "org.scalatest" %% "scalatest" % "3.1.0" % Test
  )

  val projectDeps = circe ++ Seq(lettuce, scalaTest % Test) ++ scalaMock
}
