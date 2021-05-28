package com.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.errors.Tile38Error
import com.edefritz.model.{HashResponse, PointResponse}
import io.circe.parser
import io.lettuce.core.protocol.CommandType

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

  def asObject(): Any = {
    _args = compileArgs() :+ "OBJECT"
    val response = super.exec(commandType, _args)
    response
    /*parser.decode[BaseResponse](string) match {
      //case Left(_) => BaseResponse(ok = false, PointResponse("", List()), "")
      case Right(value) => value
    }*/
  }

  def asHash(precision: Int): Either[Tile38Error, HashResponse] = {

    _args = compileArgs() ++ Seq("HASH", precision)
    val response = super.exec(commandType, _args)
    parser.decode[HashResponse](response) match {
      case Left(_) =>
        Left(super.decodeTile38Error(response))
      case Right(hashResponse: HashResponse) => Right(hashResponse)
    }
  }

  def asBounds(): String = {
    _args = compileArgs() :+ "BOUNDS"
    super.exec(commandType, _args)
  }

  def asPoint(): Option[PointResponse] = {
    _args = compileArgs() :+ "POINT"
    val point = super.exec(commandType, _args)

    parser.decode[PointResponse](point) match {
      case Left(_)                             => None
      case Right(pointResponse: PointResponse) => Some(pointResponse)
    }
  }
}
