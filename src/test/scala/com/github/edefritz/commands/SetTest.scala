package com.github.edefritz.commands

import org.scalatest.flatspec.AnyFlatSpec
import com.edefritz.commands.Set
import com.edefritz.client.Tile38Client
import org.scalamock.scalatest.MockFactory

class SetTest extends AnyFlatSpec with MockFactory {
  "Set" should "create a correct point query" in {
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
}
