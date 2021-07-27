package io.github.edefritz.commands

import io.github.edefritz.model.{
  BoundsResponse,
  HashResponse,
  ObjectResponse,
  PointResponse
}
import io.circe.parser
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.errors.Tile38Error
import io.github.edefritz.model.{
  BoundsResponse,
  HashResponse,
  ObjectResponse,
  PointResponse
}
import io.lettuce.core.protocol.CommandType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Get(key: String, id: String)(implicit tile38Client: Tile38Client)
    extends Tile38Command {

  private var _args: Seq[Any] = Seq[Any](key, id)
  private var _fields = false
  val commandType = CommandType.GET

  private def unpackFields(): Unit = {
    if (_fields) _args = _args :+ "WITHFIELDS"
  }

  // compose new seq here instead of returning modified one here
  def compileArgs() = {
    unpackFields()
    _args
  }

  def withFields(): Get = {
    _fields = true
    this
  }

  def asObject(): Future[Either[Tile38Error, ObjectResponse]] = {
    _args = compileArgs() :+ "OBJECT"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[ObjectResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(objectResponse: ObjectResponse) => Right(objectResponse)
      }
    })
  }

  def asHash(precision: Int): Future[Either[Tile38Error, HashResponse]] = {
    _args = compileArgs() ++ Seq("HASH", precision)
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[HashResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(hashResponse: HashResponse) => Right(hashResponse)
      }
    })
  }

  def asBounds(): Future[Either[Tile38Error, BoundsResponse]] = {
    _args = compileArgs() :+ "BOUNDS"
    val response = super.execAsync(commandType, _args)

    response.map(r => {
      parser.decode[BoundsResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(boundsResponse: BoundsResponse) => Right(boundsResponse)
      }
    })
  }

  def asPoint(): Future[Either[Tile38Error, PointResponse]] = {
    _args = compileArgs() :+ "POINT"
    val response = super.execAsync(commandType, _args)
    response.map(r => {
      parser.decode[PointResponse](r) match {
        case Left(_)                             => Left(super.decodeTile38Error(r))
        case Right(pointResponse: PointResponse) => Right(pointResponse)
      }
    })

  }
}
