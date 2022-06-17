package io.github.edefritz.commands

import io.github.edefritz.commands.GetCommand._
import io.github.edefritz.commands.OutputCommand.OutputCommandArgument
import io.github.edefritz.commands.SetCommand.{Point, SetCommandArgument, SetCommandInputFormat}
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{CommandArgs, CommandType, ProtocolKeyword}

import scala.jdk.CollectionConverters._
import scala.util.{Success, Try}

sealed trait Tile38Command {
  val protocolKeyword: ProtocolKeyword
  def compileArguments(): Try[CommandArgs[String, String]]
}

final case class SetCommand(
                             key: String,
                             id: String,
                             inputFormat: SetCommandArgument with SetCommandInputFormat
                           ) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = CommandType.SET

  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val args = new CommandArgs(StringCodec.UTF8)
    args.add(key)
    args.add(id)
    inputFormat match {
      case Point(lat, lon) => args.add(inputFormat.keyword).add(lat).add(lon)
      // TODO: add other input command argument types
      case other => args.add(other.keyword)
    }
    Success(args)
  }
}

object SetCommand {
  sealed trait SetCommandArgument {
    val keyword: String
  }

  sealed trait SetCommandInputFormat

  final case class Point(lat: Double, lon: Double) extends SetCommandArgument with SetCommandInputFormat {
    override val keyword: String = "POINT"
  }
}

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
      case other => args.add(other.keyword)
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
