package com.github.edefritz.commands

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec

class SetTest extends AnyFlatSpec with MockFactory with ScalaFutures {

  /*"Set" should "create a correct point query" in {
    // ARRANGE
    val client = mock[Tile38Client]

    // ACT
    val args = Set("fleet", "1")(client)
      .point(1.0, 2.0)
      .compileArgs()

    // ASSERT
    assert(args == List("fleet", "1", "POINT", 1.0, 2.0))
  }

  it should "create a correct object query" in {
    // ARRANGE
    val client = mock[Tile38Client]
    val geojson =
      "{\"type\": \"Feature\", \"properties\": {}, \"geometry\": {\"type\": \"Point\", \"coordinates\": [11.25, 52.9]}}"

    // ACT
    val args = Set("fleet", "1")(client)
      .geojson(geojson)
      .compileArgs()

    // ASSERT
    assert(args == List("fleet", "1", "OBJECT", geojson))
  }

  it should "compose args in the right order" in {
    // ARRANGE
    val client = mock[Tile38Client]

    // ACT
    val args = Set("fleet", "1")(client)
      .withFields(Map("myField" -> 111.0, "myOtherField" -> 222.0))
      .ex(100)
      .nx()
      .compileArgs()

    // ASSERT
    assert(
      args == List(
        "fleet",
        "1",
        "FIELD",
        "myField",
        111.0,
        "FIELD",
        "myOtherField",
        222.0,
        "EX",
        100,
        "NX"
      )
    )
  }

  "SetTest" should "return a successful response when setting a point" in {
    val client = new Tile38Client("redis://localhost:9851")
    val key = "fleet"
    // ACT
    val point: Future[Either[Tile38Error, SuccessfulOperationResponse]] =
      client.set(key, "1").point(1, 2).exec()

    // ASSERT
    whenReady(point) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.ok
        )
    }
  }

  it should "set a string value" in {
    val client = new Tile38Client("redis://localhost:9851")
    val key = "fleet"
    // ACT
    val string: Future[Either[Tile38Error, SuccessfulOperationResponse]] =
      client.set(key, "1:name").string("John Denton").exec()

    // ASSERT
    whenReady(string) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.ok
        )
    }
  }*/
}
