package com.github.edefritz.commands

import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.{FSet, Set}
import io.github.edefritz.errors.{Tile38Error, Tile38IdNotFoundError}
import io.github.edefritz.model.{BaseResponse, SuccessfulOperationResponse}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Future

class FSetTest
    extends AnyFlatSpec
    with MockFactory
    with ScalaFutures
    with IntegrationPatience {
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "FSet" should "create a correct fset query" in {
    // ARRANGE
    val client = mock[Tile38Client]

    // ACT
    val args =
      FSet("fleet", "1", Map("someField" -> 1.0, "someOtherField" -> 2.0))(
        client
      ).compileArgs()

    // ASSERT
    assert(args == List("fleet", "1", "someField", 1.0, "someOtherField", 2.0))
  }

  it should "return a successful response when setting a field for an existing object" in {
    val client = new Tile38Client("redis://localhost:9851")
    val key = "fleet"
    // ACT
    val fset: Future[Either[Tile38Error, BaseResponse]] =
      client
        .fset(key, "1", Map("someField" -> 1.0, "someOtherField" -> 2.0))
        .exec()

    // ASSERT
    whenReady(fset) {
      case Left(something) => println(something)
      case Right(value) =>
        assert(
          value.isInstanceOf[SuccessfulOperationResponse]
        )
    }
  }

  it should "return an error response if trying to set a field on an unknown id" in {
    val client = new Tile38Client("redis://localhost:9851")
    val key = "fleet"
    // ACT
    val fset: Future[Either[Tile38Error, BaseResponse]] =
      client
        .fset(
          key,
          "some_unknown_id",
          Map("someField" -> 1.0, "someOtherField" -> 2.0)
        )
        .exec()

    // ASSERT
    whenReady(fset) {
      case Left(value)  => assert(value.isInstanceOf[Tile38IdNotFoundError])
      case Right(value) => fail()
    }
  }
}
