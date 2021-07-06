package com.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.errors.Tile38Error
import com.edefritz.model.{
  BoundsResponse,
  HashResponse,
  ObjectResponse,
  PointResponse,
  PointsResponse,
  SeqBoundsResponse
}
import io.circe.parser
import io.lettuce.core.protocol.CommandType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Scan(key: String)(implicit tile38Client: Tile38Client)
    extends Tile38Command {

  private var _args: Seq[Any] = Seq[Any](key)
  private var _fields = false
  val commandType = CommandType.SCAN

  private def unpackFields(): Unit = {
    if (_fields) _args = _args :+ "WITHFIELDS"
  }

  // compose new seq here instead of returning modified one here
  def compileArgs() = {
    unpackFields()
    _args
  }

  def withFields(): Scan = {
    _fields = true
    this
  }

  def asObjects(): Future[Either[Tile38Error, ObjectResponse]] = {
    _args = compileArgs() :+ "OBJECTS"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[ObjectResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(objectResponse: ObjectResponse) => Right(objectResponse)
      }
    })
  }

  def asHashes(precision: Int): Future[Either[Tile38Error, HashResponse]] = {
    _args = compileArgs() ++ Seq("HASHES", precision)
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[HashResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(hashResponse: HashResponse) => Right(hashResponse)
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

  // TODO: make other response types work as well
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
