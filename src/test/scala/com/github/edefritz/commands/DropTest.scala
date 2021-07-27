package com.github.edefritz.commands

import io.github.edefritz.model._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.errors.Tile38KeyNotFoundError
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DropTest extends AnyFlatSpec with Eventually with IntegrationPatience {

  val client = new Tile38Client("redis://localhost:9851")

  "Drop command" should "drop a key when executed" in {
    // ARRANGE
    val key = "fleet"
    Await.result(client.set(key, "1").point(1, 2).exec(), Duration.Inf)

    // ACT
    val drop = Await.result(client.drop(key).exec(), Duration.Inf)

    val objects = client.get(key, "1").asPoint()

    // ASSERT
    assert(drop.isRight)

    whenReady(objects) {
      case Left(_: Tile38KeyNotFoundError) => succeed
      case Right(_) =>
        fail
    }
  }

}
