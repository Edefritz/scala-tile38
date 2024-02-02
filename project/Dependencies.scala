import sbt._

object Dependencies {
  val lettuce = "io.lettuce" % "lettuce-core" % "5.0.2.RELEASE"

  val scalatest: Seq[ModuleID] = Seq("org.scalatest" %% "scalatest" % "3.2.15" % "test")

  val circeVersion = "0.14.1"
  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-shapes"
  ).map(_ % circeVersion)

  val catsVersion = "2.9.0"
  val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % catsVersion
  )

  val catsEffectVersion = "3.4.5"
  val catsEffect: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion
  )

  val projectDeps: Seq[sbt.ModuleID] = circe ++ cats ++ catsEffect ++ Seq(lettuce) ++ scalatest
}
