package io.github.edefritz

import cats.effect._
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.SetCommand.Exists
import io.github.edefritz.commands.{ GetCommand, SetCommand }

object Application extends IOApp {

  val connection               = "redis://localhost:9851"
  val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

  val geojson: String =
    """
       {
        "type": "Point",
        "coordinates": [
          11.25,
          53.33
        ]
      }
      """.stripMargin

  override def run(args: List[String]): IO[ExitCode] =
    for {
      //input <- client.exec(SetCommand("fleet", "truck1", inputFormat = SetCommand.Point(1, 2, Some(3))))
//      input <- client.exec(
//        SetCommand(
//          key = "fleet",
//          id = "truck1",
//          inputFormat = SetCommand.Point(1, 2),
//          fields = Map("someotherfield" -> 1.0, "bla" -> 2.0)
//        )
//      )
      //input <- client.exec(SetCommand("fleet", "truck1", inputFormat = SetCommand.SetString("MyString")))
      input <- client.exec(
        SetCommand("fleet", "truck3", inputFormat = SetCommand.Point(1, 2), ex = 1, setCondition = Some(Exists()))
      )
      //input  <- client.exec(SetCommand("fleet", "truck1", inputFormat = SetCommand.Hash("9tbnwg")))
      result <- client.exec(GetCommand("fleet", "truck3", withFields = true, outputFormat = GetCommand.Object))
      _      <- IO.delay(print(result))
    } yield ExitCode.Success

}
