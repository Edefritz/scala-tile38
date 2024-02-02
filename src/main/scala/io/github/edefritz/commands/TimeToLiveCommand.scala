package io.github.edefritz.commands
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{ CommandArgs, ProtocolKeyword }

import scala.util.{ Success, Try }

final case class TimeToLiveCommand(
    key: String,
    id: String
) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = TimeToLiveCommandType

  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val args = new CommandArgs(StringCodec.UTF8)
    args.add(key)
    args.add(id)
    Success(args)
  }
}
