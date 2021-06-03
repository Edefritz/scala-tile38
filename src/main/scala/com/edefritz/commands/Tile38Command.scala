package com.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.errors._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.parser
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{CommandArgs, ProtocolKeyword}

import scala.concurrent.Future

trait Tile38Command {
  def argsSeqToRedisArgs(seq: Seq[Any]): CommandArgs[String, String] = {
    val codec = StringCodec.UTF8
    val redisArgs = new CommandArgs(codec)

    seq.foreach {
      case s: String => redisArgs.add(s)
      case d: Double => redisArgs.add(d)
      case i: Int    => redisArgs.add(i)
    }
    redisArgs
  }

  def decodeTile38Error(response: String): Tile38Error = {
    parser.decode[Tile38GenericError](response) match {
      case Right(genericError: Tile38GenericError) => {

        genericError.err match {
          case "key not found" =>
            Tile38KeyNotFoundError(
              genericError.ok,
              genericError.err,
              genericError.elapsed
            )
          case "id not found" =>
            Tile38IdNotFoundError(
              genericError.ok,
              genericError.err,
              genericError.elapsed
            )
          case _ => Tile38ResponseDecodingError("unexpected error message")
        }
      }
      case Left(error) => Tile38ResponseDecodingError(error.toString)
    }
  }

  def exec(commandType: ProtocolKeyword, args: Seq[Any])(implicit
      tile38Client: Tile38Client
  ): String = {
    val redisArgs = argsSeqToRedisArgs(args)
    tile38Client.dispatch(commandType, redisArgs)
  }

  def execAsync(commandType: ProtocolKeyword, args: Seq[Any])(implicit
      tile38Client: Tile38Client
  ): Future[String] = {
    val redisArgs = argsSeqToRedisArgs(args)
    tile38Client.dispatchAsync(commandType, redisArgs)
  }
}
