package io.github.edefritz

import cats.effect._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.{GetCommand, SetCommand}

object Application extends IOApp {

  val connection = "redis://localhost:9851"
  val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      input <- client.exec(SetCommand("fleet", "truck1", inputFormat = SetCommand.Point(1,2)))
      result <-
        client.exec(GetCommand("fleet", "truck1", outputFormat = GetCommand.Object))
      _ <- IO.delay(print(input, result))
    } yield ExitCode.Success

}
