package com.github.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.model._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec

class GetTest extends AnyFlatSpec with BeforeAndAfterAll {

  val client = new Tile38Client("redis://localhost:9851")

  override def beforeAll(): Unit = {
    client.set("fleet", "1").point(1, 2).exec()
    client
      .set("fleet", "2")
      .geojson(
        "{\"type\": \"Point\", \"coordinates\": [2, 1]}"
      )
      .exec()
  }

  "Set" should "return a correct bounds response" in {
    // ARRANGE
    val expectedOutput = Bounds(ne = LatLon(1, 2), sw = LatLon(1, 2))

    // ACT
    val response = client.get("fleet", "1").asBounds()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail()
      case Right(value) =>
        assert(
          value.bounds == expectedOutput
        )
    }

  }

  it should "return a correct point response" in {
    // ARRANGE
    val expectedOutput = Point(1, 2)

    // ACT
    val response = client.get("fleet", "1").asPoint()

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.point == expectedOutput
        )
    }
  }

  it should "return a correct hash response" in {
    // ARRANGE
    val expectedOutput = "s01mt"

    // ACT
    val response = client.get("fleet", "1").asHash(5)

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.hash == expectedOutput
        )
    }
  }

  // TODO: Make sure this also reflects geojson feature with properties
  it should "return a correct object response" in {
    // ARRANGE
    val expectedOutput = GeoJsonPoint(Coordinates(2, 1))

    // ACT
    val response = client.get("fleet", "2").asObject()

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.`object` == expectedOutput
        )
    }
  }

}
