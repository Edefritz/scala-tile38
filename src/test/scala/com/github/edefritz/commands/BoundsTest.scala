package com.github.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.model._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}

class BoundsTest
    extends AnyFlatSpec
    with BeforeAndAfterAll
    with Eventually
    with IntegrationPatience {

  val client = new Tile38Client("redis://localhost:9851")
  val key = "fleet_bounds"

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  override def beforeAll(): Unit = {
    Await.result(client.set(key, "1").point(1, 2).exec(), Duration.Inf)
    Await.result(client.set(key, "2").point(2, 1).exec(), Duration.Inf)
  }

  "BoundsTest" should "return a correct bounds response" in {
    // ARRANGE
    val expectedOutput = Polygon(
      List(
        List(
          Coordinates(1, 1),
          Coordinates(2, 1),
          Coordinates(2, 2),
          Coordinates(1, 2),
          Coordinates(1, 1)
        )
      )
    )

    // ACT
    val response = client.bounds(key).exec()

    // ASSERT
    whenReady(response) {
      case Left(_) => fail()
      case Right(value) =>
        assert(
          value.bounds == expectedOutput
        )
    }
  }

}
