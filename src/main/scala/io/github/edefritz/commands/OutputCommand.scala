package io.github.edefritz.commands

import io.github.edefritz.commands.OutputCommand.OutputCommandArgument
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{ CommandArgs, ProtocolKeyword }

import scala.util.{ Success, Try }

final case class OutputCommand(format: OutputCommandArgument) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = OutputCommandType

  override def compileArguments(): Try[CommandArgs[String, String]] =
    Success(new CommandArgs[String, String](StringCodec.UTF8).add(format.value))
}

object OutputCommand {
  sealed trait OutputCommandArgument {
    val value: String
  }
  final object Json extends OutputCommandArgument {
    override val value: String = "json"
  }
  // We don't support parsing Resp responses
  final object Resp extends OutputCommandArgument {
    override val value: String = "resp"
  }
}
