package com.github.edefritz.commands
import cats.effect._
import io.github.edefritz.Application.connection
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.GetCommand
import io.github.edefritz.responses.{Point, PointResponse}
import weaver._

object GetTest extends SimpleIOSuite {

  // TODO: Share single client (https://disneystreaming.github.io/weaver-test/docs/resources)
  // TODO: Run tile38 in docker before test (using expected data from appendonly.aof)

  test("Test get command") {
    // ARRANGE
    val client = Tile38Client.forAsync[IO](connection)
    val expectedPoint = Point(1,1)

    // ACT
    for {
      result <-
        client.exec(GetCommand("fleet", "truck1", outputFormat = GetCommand.Point))
    } yield result match {
      // ASSERT
      case PointResponse(_, _, point) => expect(point == expectedPoint)
    }
  }
}
