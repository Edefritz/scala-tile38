package com.github.edefritz.commands

import io.github.edefritz.client.Tile38Client
import io.github.edefritz.model.{
  Bounds,
  Coordinates,
  Feature,
  FeatureCollection,
  GeoJsonPoint,
  LatLon,
  Point
}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec

class GetTest extends AnyFlatSpec with BeforeAndAfterAll {

  val client = new Tile38Client("redis://localhost:9851")

  override def beforeAll(): Unit = {
    client.set("fleet", "1").point(1, 2).exec()
    client
      .set("fleet", "geojson-feature")
      .geojson(
        "{\"type\": \"Feature\", \"properties\": {}, \"geometry\": {\"type\": \"Point\", \"coordinates\": [2, 1]}}"
      )
      .exec()
    client
      .set("fleet", "geojson-point")
      .geojson(
        "{\"type\": \"Point\", \"coordinates\": [2, 1]}"
      )
      .exec()
    client
      .set("fleet", "geojson-featurecollection")
      .geojson(
        "{\"type\": \"FeatureCollection\", \"features\": [{\"type\": \"Feature\", \"properties\": {}, \"geometry\": {\"type\": \"Point\", \"coordinates\": [2, 1]}}]}"
      )
      .exec()
  }

  "GetTest" should "return a correct bounds response" in {
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

  it should "return a correct object feature response" in {
    // ARRANGE
    val expectedOutput = Feature(GeoJsonPoint(Coordinates(2, 1)), Some(Map()))

    // ACT
    val response = client.get("fleet", "geojson-feature").asObject()

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.`object` == expectedOutput
        )
    }
  }

  it should "return a correct object point response" in {
    // ARRANGE
    val expectedOutput = GeoJsonPoint(Coordinates(2, 1))

    // ACT
    val response = client.get("fleet", "geojson-point").asObject()

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.`object` == expectedOutput
        )
    }
  }

  it should "return a correct object featurecollection response" in {
    // ARRANGE

    val expectedOutput =
      FeatureCollection(
        List(Feature(GeoJsonPoint(Coordinates(2, 1)), Some(Map())))
      )

    // ACT
    val response = client.get("fleet", "geojson-featurecollection").asObject()

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.`object` == expectedOutput
        )
    }
  }

  // TODO: Make sure geojson properties are parsed too
  // The issue is that properties can be Map[String, Any] but circe cannot decode Any
  // https://github.com/circe/circe/issues/216

}
