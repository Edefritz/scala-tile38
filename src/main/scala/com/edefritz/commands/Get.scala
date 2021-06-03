package com.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.errors.Tile38Error
import com.edefritz.model.{BoundsResponse, HashResponse, PointResponse}
import io.circe.parser
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

  // TODO: take care of parsing geojson
  def asObject(): Any = {
    _args = compileArgs() :+ "OBJECT"
    val response = super.exec(commandType, _args)
    response
    /*parser.decode[BaseResponse](string) match {
      //case Left(_) => BaseResponse(ok = false, PointResponse("", List()), "")
      case Right(value) => value
    }*/
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

  // TODO: make futures work
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
