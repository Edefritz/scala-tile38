package io.github.edefritz.test.commands

import cats.effect._
import cats.effect.unsafe.implicits.global
import io.github.edefritz.Application.connection
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.SetCommand.{ Exists, NotExists, SetCondition, Point => SetPoint }
import io.github.edefritz.commands.{ GetCommand, SetCommand, TimeToLiveCommand }
import io.github.edefritz.responses._
import io.github.edefritz.test.util.JsonAssertions
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AsyncFunSuite

import java.util.UUID

class SetTest extends AsyncFunSuite with BeforeAndAfterAll with JsonAssertions {

  private val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

  test("Test set valid point command") {
    // ARRANGE
    val key = UUID.randomUUID().toString
    val id  = UUID.randomUUID().toString

    val setCommand = SetCommand(
      key,
      id,
      inputFormat = SetPoint(2, 1)
    )

    val getCommand = GetCommand(
      key,
      id,
      outputFormat = GetCommand.Point
    )

    for {
      // ACT
      _         <- client.exec(setCommand).unsafeToFuture()
      getResult <- client.exec(getCommand).unsafeToFuture()

    } yield getResult match {
      // ASSERT
      case PointResponse(_, _, point, _) =>
        assert(point.lat == 2)
        assert(point.lon == 1)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Test set point with z coordinate command") {
    // ARRANGE
    val key = UUID.randomUUID().toString
    val id  = UUID.randomUUID().toString

    val setCommand = SetCommand(
      key,
      id,
      inputFormat = SetPoint(1000, 1, Some(1))
    )

    val getCommand = GetCommand(
      key,
      id,
      outputFormat = GetCommand.Point
    )

    for {
      // ACT
      _         <- client.exec(setCommand).unsafeToFuture()
      getResult <- client.exec(getCommand).unsafeToFuture()

    } yield getResult match {
      // ASSERT
      case PointResponse(_, _, point, _) =>
        assert(point.z.contains(1))
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }
  }

  test("Test set point with XX modifier should fail when item does not exist") {
    // ARRANGE
    val key = UUID.randomUUID().toString
    val id  = UUID.randomUUID().toString

    val setCommand = SetCommand(
      key,
      id,
      inputFormat = SetPoint(1, 1),
      setCondition = Some(Exists())
    )

    for {
      // ACT
      setResult <- client.exec(setCommand).unsafeToFuture()
    } yield setResult match {
      // ASSERT
      case Tile38ReponseError(_, _, err) =>
        assert(err.contains("id not found"))
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }
  }

  test("Test set point with NX modifier should fail when item exists") {
    // ARRANGE
    val key = UUID.randomUUID().toString
    val id  = UUID.randomUUID().toString

    val setCommand = SetCommand(
      key,
      id,
      inputFormat = SetPoint(1, 1),
      setCondition = Some(NotExists())
    )

    for {
      // ACT
      _         <- client.exec(setCommand).unsafeToFuture()
      secondSet <- client.exec(setCommand).unsafeToFuture()
    } yield secondSet match {
      // ASSERT
      case Tile38ReponseError(_, _, err) =>
        assert(err.contains("id already exists"))
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }
  }

  test("Test set point with EX modifier should return TTL > 0") {
    // ARRANGE
    val key = UUID.randomUUID().toString
    val id  = UUID.randomUUID().toString

    val setCommand = SetCommand(
      key,
      id,
      inputFormat = SetPoint(1, 1),
      ex = 100
    )

    val ttlCommand = TimeToLiveCommand(
      key,
      id
    )

    for {
      // ACT
      _         <- client.exec(setCommand).unsafeToFuture()
      secondSet <- client.exec(ttlCommand).unsafeToFuture()
    } yield secondSet match {
      // ASSERT
      case TimeToLiveResponse(_, _, ttl) =>
        assert(ttl > 0)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }
  }

}
