package com.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.customCommandTypes.{BoundsCommandType, OutputCommandType}
import com.edefritz.errors.Tile38Error
import com.edefritz.model.{KeyBoundsResponse, ObjectResponse, OutputType}
import io.circe.parser
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
