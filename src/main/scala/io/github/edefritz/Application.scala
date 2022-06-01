package io.github.edefritz

import cats.effect._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.GetCommand

object Application extends IOApp {

  val connection = "redis://localhost:9851"

  override def run(args: List[String]): IO[ExitCode] =
    for {
      result <-
        Tile38Client.forAsync[IO](connection).exec(GetCommand("fleet", "truck1", outputFormat = GetCommand.Point))
      _ <- IO.delay(print(result))
    } yield ExitCode.Success

}
