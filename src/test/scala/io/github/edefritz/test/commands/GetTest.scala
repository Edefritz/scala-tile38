package io.github.edefritz.test.commands

import cats.effect._
import cats.effect.unsafe.implicits.global
import io.circe.Json
import io.github.edefritz.Application.connection
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.{ GetCommand, SetCommand }
import io.github.edefritz.commands.SetCommand.{ Point => SetPoint }
import io.github.edefritz.responses._
import io.github.edefritz.test.util.JsonAssertions
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AsyncFunSuite

import java.util.UUID

class GetTest extends AsyncFunSuite with BeforeAndAfterAll with JsonAssertions {

  private val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)
  // ARRANGE
  private val key = UUID.randomUUID().toString
  private val id  = UUID.randomUUID().toString

  override def beforeAll(): Unit = {
    val setCommand = SetCommand(
      key,
      id,
      inputFormat = SetPoint(1, 1),
      fields = Map("speed" -> 10)
    )

    for {
      result <- client.exec(setCommand).unsafeToFuture()
    } yield result
  }

  test("Test get point command") {
    // ARRANGE
    val expectedPoint = Point(1, 1)

    // ACT
    for {
      _ <- client.exec(SetCommand(key, id, inputFormat = SetPoint(1, 1))).unsafeToFuture()
      result <- client.exec(GetCommand(key, id, outputFormat = GetCommand.Point)).unsafeToFuture()
    } yield result match {
      // ASSERT
      case PointResponse(_, _, point, _) =>
        assert(point == expectedPoint)
      case other => fail(s"Didn't receive a proper point response: ${other.toString}")
    }

  }

  test("Test get object command") {
    // ARRANGE
    val jsonString           = """
{
  "type": "Point",
  "coordinates": [
    1,
    1
  ]
}
"""
    val expectedObject: Json = parseOrElse(jsonString)
    // ACT
    for {
      result <- client.exec(GetCommand(key, id, outputFormat = GetCommand.Object)).unsafeToFuture()
    } yield result match {
      // ASSERT
      case o: ObjectResponse =>
        assert(o.`object` == expectedObject)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Test get bounds command") {
    // ARRANGE
    val expectedOutput: BoundsResponse = BoundsResponse(ok = true, "", Bounds(LatLon(1.0, 1.0), LatLon(1.0, 1.0)))
    // ACT
    for {
      result <- client.exec(GetCommand(key, id, outputFormat = GetCommand.Bounds)).unsafeToFuture()
    } yield result match {
      // ASSERT
      case o: BoundsResponse =>
        assert(o.bounds == expectedOutput.bounds)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Test get hash command") {
    // ARRANGE
    val expectedOutput: HashResponse = HashResponse(ok = true, "", "s00tw")
    // ACT
    for {
      result <- client.exec(GetCommand(key, id, outputFormat = GetCommand.Hash(5))).unsafeToFuture()
    } yield result match {
      // ASSERT
      case o: HashResponse =>
        assert(o.hash == expectedOutput.hash)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Test get fields command") {
    // ARRANGE
    val expectedOutput: Map[String, Double] = Map[String, Double]("speed" -> 10)
    // ACT
    for {
      result <- client.exec(GetCommand(key, id, withFields = true, outputFormat = GetCommand.Point)).unsafeToFuture()
    } yield result match {
      // ASSERT
      case o: PointResponse =>
        assert(o.fields.contains(expectedOutput))
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }
}
