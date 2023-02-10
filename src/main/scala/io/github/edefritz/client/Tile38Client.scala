package io.github.edefritz.client

import cats.effect.kernel.{ Async, Resource }
import cats.syntax.all._
import io.circe.parser
import io.github.edefritz.commands._
import io.github.edefritz.responses.Tile38Response
import io.github.edefritz.responses.Tile38Response.decoder
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.{ CommandArgs, ProtocolKeyword }

trait Tile38Client[F[_]] {
  def exec(command: Tile38Command): F[Tile38Response]
}

object Tile38Client {

  // TODO: Can we handle serialization with codec?
  private final val Codec: StringCodec = StringCodec.UTF8
  private final val OutputJsonCommand  = OutputCommand(OutputCommand.Json)

  def forAsync[F[_]: Async](connectionString: String): Tile38Client[F] =
    new Tile38Client[F] {
      private lazy val client: RedisClient = RedisClient.create(connectionString)
      private lazy val connectionResource: Resource[F, StatefulRedisConnection[String, String]] =
        Resource.make(Async[F].blocking(client.connect(Codec)))(client => Async[F].blocking(client.close()))

      override def exec(command: Tile38Command): F[Tile38Response] = {
        def dispatchCommand(
            protocolKeyword: ProtocolKeyword,
            commandArgs: CommandArgs[String, String],
            valueOutput: ValueOutput[String, String] = new ValueOutput(Codec)
        )(client: StatefulRedisConnection[String, String]): F[String] = {
          val eventualCommandExecution = Async[F].delay {
            client.async().dispatch(protocolKeyword, valueOutput, commandArgs).toCompletableFuture
          }
          Async[F].fromCompletableFuture(eventualCommandExecution)
        }

        connectionResource.use { client =>
          for {
            outputArgs      <- Async[F].fromTry(OutputJsonCommand.compileArguments())
            _               <- dispatchCommand(OutputJsonCommand.protocolKeyword, outputArgs)(client)
            args            <- Async[F].fromTry(command.compileArguments())
            response        <- dispatchCommand(command.protocolKeyword, args)(client)
            decodedResponse <- Async[F].fromEither(parser.decode(response))
          } yield decodedResponse
        }
      }
    }

}
