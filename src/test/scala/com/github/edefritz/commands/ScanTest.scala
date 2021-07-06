package com.github.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.model._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec

class ScanTest extends AnyFlatSpec with BeforeAndAfterAll {

  val client = new Tile38Client("redis://localhost:9851")

  override def beforeAll(): Unit = {
    client.set("fleet-scan", "1").point(1, 2).exec()
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

}
