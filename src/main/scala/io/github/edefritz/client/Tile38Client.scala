package io.github.edefritz.client

import io.circe.parser
import io.github.edefritz.client.Tile38Client.Codec
import io.github.edefritz.commands
import io.github.edefritz.commands._
import io.github.edefritz.errors.{Tile38Error, Tile38GenericError, Tile38IdNotFoundError, Tile38KeyNotFoundError, Tile38ResponseDecodingError}
import io.github.edefritz.model.{JsonType, OutputType, RespType, Tile38Response}
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.{CommandArgs, ProtocolKeyword}

import scala.concurrent.Future
import scala.jdk.FutureConverters.CompletionStageOps
import scala.util.{Try, Using}

class Tile38Client(connectionString: String) {
  private lazy val client: RedisClient = RedisClient.create(connectionString)


  // By default a lettuce connection would return RESP
  private var format: OutputType = RespType

  // This is needed to configure the connection to request JSON from Tile38
  private def forceJson() = {
    if (this.format != JsonType) {
      format = JsonType
      Output(JsonType)(this).exec()
    }
  }

  private def argsSeqToRedisArgs(seq: Seq[Any]): CommandArgs[String, String] = {
    val codec = StringCodec.UTF8
    val redisArgs = new CommandArgs(codec)

    seq.foreach {
      case s: String => redisArgs.add(s)
      case d: Double => redisArgs.add(d)
      case i: Int    => redisArgs.add(i)
    }
    redisArgs
  }

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
  // TODO: Include this in the trait decoder later
  private def decodeTile38Error(response: String): Tile38Error = {
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
          case error: String =>
            Tile38ResponseDecodingError(s"unexpected error message: $error")
        }
      }
      case Left(error) => Tile38ResponseDecodingError(error.toString)
    }
  }

  def execSync(command: Tile38Command): Try[Tile38Response] = {
    Using(client.connect(Codec)) { client =>
      client.sync().dispatch()
    }
  }


  def execAsync(command: Tile38Command): Future[Tile38Response] = {

  }

  def exec(
      commandType: ProtocolKeyword,
      args: CommandArgs[String, String]
  ): Future[String] = {
    async.dispatch(commandType, new ValueOutput(Codec), args).asScala
  }

}

object Tile38Client {
  final val Codec: StringCodec = StringCodec.UTF8;
}
