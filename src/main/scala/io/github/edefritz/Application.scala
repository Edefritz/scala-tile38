package io.github.edefritz

import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.GetCommand

object Application extends App {

  val connection = "redis://localhost:9851"

  val client = new Tile38Client(connection)

  val maybeResult = client.execSync(GetCommand("fleet", "truck1", point = true))

  println(maybeResult)

}
