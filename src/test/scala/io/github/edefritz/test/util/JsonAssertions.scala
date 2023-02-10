package io.github.edefritz.test.util

import io.circe.Json
import io.circe.parser._
import org.scalatest.Assertions.fail

trait JsonAssertions {
  def parseOrElse(jsonString: String): Json =
    parse(jsonString) match {
      case Right(json) => json
      case Left(failure) =>
        println(s"Error parsing $jsonString")
        fail(failure)
    }
}
