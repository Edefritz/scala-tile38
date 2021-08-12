package io.github.edefritz.commands

import io.github.edefritz.customCommandTypes.BoundsCommandType
import io.github.edefritz.model.{
  BaseResponse,
  KeyBoundsResponse,
  SuccessfulOperationResponse
}
import io.circe.parser
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.customCommandTypes.DropCommandType
import io.github.edefritz.errors.Tile38Error
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.CommandType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Drop(key: String)(implicit tile38Client: Tile38Client)
    extends Tile38Command {
  val codec = StringCodec.UTF8;
  val args = Seq(key)
  val commandType = DropCommandType

  def exec(): Future[Either[Tile38Error, SuccessfulOperationResponse]] = {
    val response = super.execAsync(commandType, args)
    response.map(r => {
      parser.decode[BaseResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(boundsResponse: SuccessfulOperationResponse) =>
          Right(boundsResponse)
      }
    })
  }

}
