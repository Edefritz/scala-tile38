package com.github.edefritz.commands

import org.scalatest.concurrent.{ Eventually, IntegrationPatience }
import org.scalatest.flatspec.AnyFlatSpec

class DropTest extends AnyFlatSpec with Eventually with IntegrationPatience {

  /*val client = new Tile38Client("redis://localhost:9851")

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
  }*/

}
