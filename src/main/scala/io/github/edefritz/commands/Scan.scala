package io.github.edefritz.commands

import io.github.edefritz.model.{
  CountResponse,
  HashesResponse,
  IdsResponse,
  ObjectsResponse,
  PointsResponse,
  SeqBoundsResponse
}
import io.circe.parser
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.errors.Tile38Error
import io.github.edefritz.model.{
  CountResponse,
  HashesResponse,
  IdsResponse,
  ObjectsResponse,
  PointsResponse,
  SeqBoundsResponse
}
import io.lettuce.core.protocol.CommandType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Scan(key: String)(implicit tile38Client: Tile38Client)
    extends Tile38Command {

  private var _args: Seq[Any] = Seq[Any](key)
  private var _where = Seq[Any]()
  private var _nofields = false
  private var _cursor = Seq("CURSOR", 0)
  private var _limit = Seq("LIMIT", 100)
  private val commandType = CommandType.SCAN

  def compileArgs(): Seq[Any] = {
    _args = _args ++ _cursor ++ _limit ++ _where
    if (_nofields) _args = _args :+ "NOFIELDS"
    _args
  }

  // TODO: How to pass infinity?
  def where(field: String, min: Double, max: Double): Scan = {
    _where = Seq("WHERE", field, min, max)
    this
  }

  // TODO: Fields are not parsed yet
  def noFields(): Scan = {
    _nofields = true
    this
  }

  def cursor(cursor: Int): Scan = {
    _cursor = Seq("CURSOR", cursor)
    this
  }

  def limit(limit: Int): Scan = {
    _limit = Seq("LIMIT", limit)
    this
  }

  def asCount(): Future[Either[Tile38Error, CountResponse]] = {
    _args = compileArgs() :+ "COUNT"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[CountResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(countResponse: CountResponse) => Right(countResponse)
      }
    })
  }

  def asIds(): Future[Either[Tile38Error, IdsResponse]] = {
    _args = compileArgs() :+ "IDS"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[IdsResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(idsResponse: IdsResponse) => Right(idsResponse)
      }
    })
  }

  def asObjects(): Future[Either[Tile38Error, ObjectsResponse]] = {
    _args = compileArgs() :+ "OBJECTS"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[ObjectsResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(objectsResponse: ObjectsResponse) => Right(objectsResponse)
      }
    })
  }

  def asHashes(precision: Int): Future[Either[Tile38Error, HashesResponse]] = {
    _args = compileArgs() ++ Seq("HASHES", precision)
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[HashesResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(hashesResponse: HashesResponse) => Right(hashesResponse)
      }
    })
  }

  def asBounds(): Future[Either[Tile38Error, SeqBoundsResponse]] = {
    _args = compileArgs() :+ "BOUNDS"
    val response = super.execAsync(commandType, _args)

    response.map(r => {
      parser.decode[SeqBoundsResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(seqBoundsResponse: SeqBoundsResponse) =>
          Right(seqBoundsResponse)
      }
    })
  }

  def asPoints(): Future[Either[Tile38Error, PointsResponse]] = {
    _args = compileArgs() :+ "POINTS"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[PointsResponse](r) match {
        case Left(e)                               => Left(super.decodeTile38Error(r))
        case Right(pointsResponse: PointsResponse) => Right(pointsResponse)
      }
    })

  }
}
