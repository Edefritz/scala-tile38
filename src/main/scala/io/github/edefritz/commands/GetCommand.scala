package io.github.edefritz.commands

import io.github.edefritz.commands.GetCommand.{ GetCommandArgument, GetCommandOutputFormat, Hash, WithFields }
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{ CommandArgs, CommandType, ProtocolKeyword }

import scala.util.{ Success, Try }

final case class GetCommand(
    key: String,
    id: String,
    withFields: Boolean = false,
    outputFormat: GetCommandArgument with GetCommandOutputFormat
) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = CommandType.GET
  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val args = new CommandArgs(StringCodec.UTF8)
    args.add(key)
    args.add(id)
    if (withFields) args.add(WithFields.keyword)
    outputFormat match {
      case Hash(precision) => args.add(outputFormat.keyword).add(precision)
      case other           => args.add(other.keyword)
    }
    Success(args)
  }
}

object GetCommand {
  sealed trait GetCommandArgument {
    val keyword: String
  }

  sealed trait GetCommandOutputFormat

  final object WithFields extends GetCommandArgument {
    override val keyword: String = "WITHFIELDS"
  }

  final object Object extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "OBJECT"
  }

  final object Point extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "POINT"
  }

  final object Bounds extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "BOUNDS"
  }

  final case class Hash(precision: Int) extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "HASH"
  }
}
