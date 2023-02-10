package io.github.edefritz

import cats.effect._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.{ GetCommand, SetCommand }

object Application extends IOApp {

  val connection               = "redis://localhost:9851"
  val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _      <- client.exec(SetCommand("fleet", "1", inputFormat = SetCommand.Point(1, 2)))
      result <- client.exec(GetCommand("fleet", "1", withFields = true, outputFormat = GetCommand.Point))
      _      <- IO.delay(print(result))
    } yield ExitCode.Success

}
