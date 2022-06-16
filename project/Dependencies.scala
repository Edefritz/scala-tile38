import sbt._

object Dependencies {
  val lettuce        = "io.lettuce"     % "lettuce-core" % "5.0.2.RELEASE"

  val circeVersion = "0.14.1"
  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-shapes"
  ).map(_ % circeVersion)

  val catsVersion = "2.7.0"
  val cats = Seq(
    "org.typelevel" %% "cats-core" % catsVersion
  )

  val catsEffectVersion = "3.3.12"
  val catsEffect = Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion
  )

  val weaver = Seq("com.disneystreaming" %% "weaver-cats" % "0.7.12" % Test)

  val projectDeps = circe ++ cats ++ catsEffect ++ Seq(lettuce) ++ weaver
}
