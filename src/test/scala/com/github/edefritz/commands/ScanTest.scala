package com.github.edefritz.commands

import io.github.edefritz.model._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.model.{
  Bounds,
  Coordinates,
  GeoJsonPoint,
  IdBounds,
  IdHash,
  IdObject,
  IdPoint,
  LatLon,
  Point
}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec

class ScanTest extends AnyFlatSpec with BeforeAndAfterAll {

  val client = new Tile38Client("redis://localhost:9851")

  override def beforeAll(): Unit = {
    client
      .set("fleet-scan", "1")
      .point(1, 2)
      .withFields(Map("speed" -> 100.0))
      .exec()
  }

  "ScanTest" should "return a correct points scan response" in {
    // ARRANGE
    val expectedOutput = IdPoint("1", Point(1, 2))

    // ACT
    val response = client.scan("fleet-scan").asPoints()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.points.head == expectedOutput
        )
    }
  }

  it should "return a correct scan bounds response" in {
    // ARRANGE
    val expectedOutput = IdBounds("1", Bounds(LatLon(1, 2), LatLon(1, 2)))

    // ACT
    val response = client.scan("fleet-scan").asBounds()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.bounds.head == expectedOutput
        )
    }
  }

  it should "return a correct scan hashes response" in {
    // ARRANGE
    val expectedOutput = IdHash("1", "s01mt")

    // ACT
    val response = client.scan("fleet-scan").asHashes(5)

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.hashes.head == expectedOutput
        )
    }
  }

  it should "return a correct objects response" in {
    // ARRANGE
    val expectedOutput = IdObject("1", GeoJsonPoint(Coordinates(2, 1)))

    // ACT
    val response = client.scan("fleet-scan").asObjects()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.objects.head == expectedOutput
        )
    }
  }

  it should "return a correct ids response" in {
    // ARRANGE
    val expectedOutput = Seq("1")

    // ACT
    val response = client.scan("fleet-scan").asIds()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.ids == expectedOutput
        )
    }
  }

  it should "return a correct count response" in {
    // ARRANGE
    val expectedOutput = 1

    // ACT
    val response = client.scan("fleet-scan").asCount()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.count == expectedOutput
        )
    }
  }

  it should "filter items using matching field in where command" in {
    // ARRANGE
    val expectedOutput = 0

    // ACT
    val response = client.scan("fleet-scan").where("speed", 0, 1).asCount()

    // ASSERT
    whenReady(response) {
      case Left(value) => fail(value.toString)
      case Right(value) =>
        assert(
          value.count == expectedOutput
        )
    }
  }

}
