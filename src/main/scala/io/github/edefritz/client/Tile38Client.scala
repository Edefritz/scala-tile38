package io.github.edefritz.client

import io.circe.parser
import io.github.edefritz.client.Tile38Client.Codec
import io.github.edefritz.commands._
import io.github.edefritz.errors._
import io.github.edefritz.responses.Tile38Response
import io.github.edefritz.responses.Tile38Response.decoder
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.CommandArgs

import scala.util.{ Try, Using }

class Tile38Client(connectionString: String) {
  private lazy val client: RedisClient = RedisClient.create(connectionString)

  def execSync(command: Tile38Command): Try[Tile38Response] = {
    def doQuery(args: CommandArgs[String, String]): Try[String] =
      Using(client.connect(Codec)) { client =>
        client
          .sync()
          .dispatch(OutputCommandType, new ValueOutput(Codec), new CommandArgs[String, String](Codec).add("json"))
        client.sync().dispatch(command.protocolKeyword, new ValueOutput(Codec), args)
      }
    for {
      args            <- command.compileArguments()
      response        <- doQuery(args)
      decodedResponse <- parser.decode(response).toTry
    } yield decodedResponse
  }

  /*  def execAsync(command: Tile38Command): Future[Tile38Response] = {}

  def exec(
      commandType: ProtocolKeyword,
      args: CommandArgs[String, String]
  ): Future[String] = {
    async.dispatch(commandType, new ValueOutput(Codec), args).asScala
  }*/

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

  // By default a lettuce connection would return RESP
  //private var format: OutputType = RespType

  // This is needed to configure the connection to request JSON from Tile38
  /*private def forceJson() = {
    if (this.format != JsonType) {
      format = JsonType
      Output(JsonType)(this).exec()
    }
  }*/

}

object Tile38Client {
  final val Codec: StringCodec = StringCodec.UTF8;
}
