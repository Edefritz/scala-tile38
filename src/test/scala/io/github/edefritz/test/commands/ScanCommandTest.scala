package io.github.edefritz.test.commands

import cats.Parallel.parSequence
import cats.effect._
import cats.effect.unsafe.implicits.global
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.commands.ScanCommand.{ Count, Cursor, Ids, Limit, Objects, Where }
import io.github.edefritz.commands.{ ScanCommand, SetCommand }
import io.github.edefritz.responses._
import io.github.edefritz.test.util.JsonAssertions
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AsyncFunSuite

import java.util.UUID
import scala.language.postfixOps

class ScanCommandTest extends AsyncFunSuite with BeforeAndAfterAll with JsonAssertions {

  val connection                       = "redis://localhost:9851"
  private val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

  test("Scan returns all objects in a collection") {
    // ARRANGE
    val keySize = 100
    val key     = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> 10))
    }
    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      outputFormat = Some(Count)
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case CountResponse(_, count, _, _) =>
        assert(count == keySize)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns all objects in a collection starting from cursor") {
    // ARRANGE
    val keySize = 100
    val cursor  = 1
    val key     = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> 10))
    }
    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      outputFormat = Some(Count),
      cursor = Some(Cursor(cursor))
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case CountResponse(_, count, _, _) =>
        assert(count == keySize - cursor)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns limited number of items with limit option") {
    // ARRANGE
    val keySize = 100
    val limit   = 10

    val key = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> 10))
    }
    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      limit = Some(Limit(limit)),
      outputFormat = Some(Objects)
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case ObjectsResponse(_, _, objects) =>
        assert(objects.asArray.get.size == limit)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }
  }

  test("Scan returns all objects in a collection with ids matching a pattern") {
    // ARRANGE
    val keySize = 100
    val key     = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> 10))
    }
    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      matchExpression = Some(ScanCommand.MatchExpression("1*")),
      outputFormat = Some(Ids)
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case IdsResponse(_, ids, _, _, _) =>
        assert(ids == List("1", "10", "100", "11", "12", "13", "14", "15", "16", "17", "18", "19"))
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns all objects and sorts them in descending order") {
    // ARRANGE
    val keySize = 100
    val key     = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> 10))
    }

    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      matchExpression = Some(ScanCommand.MatchExpression("1*")),
      outputFormat = Some(Ids),
      sort = Some(ScanCommand.Descending())
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case IdsResponse(_, ids, _, _, _) =>
        assert(ids.headOption.contains("19"))
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns all objects where field passes the filter (greater than)") {
    // ARRANGE
    val keySize = 100
    val key     = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> i))
    }

    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      where = List(Where(List("speed", ">", "50")))
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case ObjectsResponse(_, _, objects) =>
        assert(objects.asArray.get.size == 50)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns all objects where field passes multiple filters (greater than and smaller than)") {
    // ARRANGE
    val keySize = 100
    val key     = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> i))
    }

    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      where = List(
        Where(List("speed", ">", "50")),
        Where(List("speed", "<", "60"))
      )
    )

    for {
      // ACT
      _      <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case ObjectsResponse(_, _, objects) =>
        assert(objects.asArray.get.size == 9)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns all objects where field passes multiple filters (equals)") {
    // ARRANGE
    val keySize = 100
    val key = UUID.randomUUID().toString
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Point(1, 2), fields = Map("speed" -> i))
    }

    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      where = List(
        Where(List("speed", "==", "50"))
      )
    )

    for {
      // ACT
      _ <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case ObjectsResponse(_, _, objects) =>
        assert(objects.asArray.get.size == 1)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

  test("Scan returns all objects where field passes object properties filter (equals)") {
    // ARRANGE
    val keySize = 1
    val key = UUID.randomUUID().toString
    val geojson = """{"type":"Feature","geometry":{"type":"Point","coordinates":[1,2]},"properties":{"driver":"John"}}"""
    val effects = 1 to keySize map { i =>
      SetCommand(key, i.toString, inputFormat = SetCommand.Object(geojson))
    }

    val commands = effects.map(client.exec(_)).toList

    val scanCommand = ScanCommand(
      key,
      where = List(
        Where(List("properties.driver", "==", "Jack"))
      )
    )

    for {
      // ACT
      _ <- parSequence(commands).unsafeToFuture()
      result <- client.exec(scanCommand).unsafeToFuture()
    } yield result match {
      // ASSERT
      case ObjectsResponse(_, _, objects) =>
        assert(objects.asArray.get.size == keySize)
      case other => fail(s"Didn't receive a proper response: ${other.toString}")
    }

  }

}
