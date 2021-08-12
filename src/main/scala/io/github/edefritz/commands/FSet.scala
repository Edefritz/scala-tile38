package io.github.edefritz.commands

import io.circe.parser
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.customCommandTypes.FSetCommandType
import io.github.edefritz.errors.Tile38Error
import io.github.edefritz.model.{BaseResponse, SuccessfulOperationResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FSet(key: String, id: String, fields: Map[String, Double])(implicit
    tile38Client: Tile38Client
) extends Tile38Command {

  private var _args: Seq[Any] = Seq[Any](key, id)
  private var _xx: Boolean = false
  private val _fields: Map[String, Double] = fields

  def xx(): FSet = {
    this._xx = true
    this
  }

  private def unpackFields(): Unit = {
    _fields.foreach(f => {
      _args = _args ++ Seq(f._1, f._2)
    })
  }

  private def unpackXX(): Unit = {
    if (_xx) _args = _args :+ "XX"
  }

  // compose new seq here instead of returning modified one here
  def compileArgs() = {
    unpackXX()
    unpackFields()
    _args
  }

  // TODO: Add tests
  def exec(): Future[Either[Tile38Error, BaseResponse]] = {
    val response = super.execAsync(FSetCommandType, compileArgs())(tile38Client)
    response.map(r => {
      parser.decode[BaseResponse](r) match {
        case Right(response: SuccessfulOperationResponse) =>
          Right(response)
        case Left(_) =>
          Left(super.decodeTile38Error(r))
      }
    })
  }
}
