package io.github.edefritz.commands

import io.github.edefritz.customCommandTypes.BoundsCommandType
import io.github.edefritz.model.ObjectResponse
import io.circe.parser
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.customCommandTypes.BoundsCommandType
import io.github.edefritz.errors.Tile38Error
import io.github.edefritz.model.KeyBoundsResponse
import io.lettuce.core.codec.StringCodec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Bounds(key: String)(implicit tile38Client: Tile38Client)
    extends Tile38Command {
  val codec = StringCodec.UTF8;
  val args = Seq(key)
  val commandType = BoundsCommandType

  def exec(): Future[Either[Tile38Error, KeyBoundsResponse]] = {
    val response = super.execAsync(commandType, args)
    response.map(r => {
      parser.decode[KeyBoundsResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(boundsResponse: KeyBoundsResponse) => Right(boundsResponse)
      }
    })
  }

}
