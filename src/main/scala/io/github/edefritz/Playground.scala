package io.github.edefritz

import cats.Parallel.parSequence
import cats.effect._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.ScanCommand.{Ascending, Descending, MatchExpression}
import io.github.edefritz.commands.{GetCommand, ScanCommand, SetCommand}

object Playground extends IOApp {

  val connection               = "redis://localhost:9851"
  val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

  override def run(args: List[String]): IO[ExitCode] = {

    val effects = 1 to 100 map { i =>
      SetCommand("fleet", i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> 10))
    }

    val commands = effects.map(client.exec(_)).toList



    for {
      _      <- IO.delay(println("Starting"))
      result <- parSequence(commands)
      result2 <- client.exec(GetCommand("fleet", "1", withFields = true))
      result3 <- client.exec(ScanCommand("fleet", matchExpression = Some(MatchExpression("1*"))))
      _      <- IO.delay(println(result))
      _     <- IO.delay(println(result2))
        _     <- IO.delay(println(result3))
    } yield ExitCode.Success

//    for {
//      _      <- client.exec(SetCommand("fleet", "1", inputFormat = SetCommand.Point(1, 2)))
//      result <- client.exec(GetCommand("fleet", "1", withFields = true, outputFormat = GetCommand.Point))
//      _      <- IO.delay(print(result))
//    } yield ExitCode.Success
  }

}
