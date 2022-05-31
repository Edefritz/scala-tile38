package io.github.edefritz.commands

import io.lettuce.core.protocol.{CommandArgs, CommandType, ProtocolKeyword}
import GetCommand._
import io.lettuce.core.codec.StringCodec

import scala.util.{Failure, Success, Try}

sealed trait Tile38Command {
  val protocolKeyword: ProtocolKeyword
  def compileArguments(): Try[CommandArgs[String, String]]
}

final case class GetCommand(key: String, id: String,
                            withFields: Boolean = false,
                            `object`: Boolean = false,
                            point: Boolean = false,
                            bounds: Boolean = false,
                            hash: Option[Int] = None) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = CommandType.GET
  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val redisArgs = new CommandArgs(StringCodec.UTF8)
    redisArgs.add(key).add(id)
    if(withFields) redisArgs.add(WithFields.keyword)
    val triedCommandArgument: Try[GetCommandArgument] = (`object`, point, bounds, hash) match {
      case (true, false, false, None) => Success(Object)
      case (false, true, false, None) => Success(Point)
      case (false, false, true, None) => Success(Bounds)
      case (false, false, false, Some(precision)) => Success(Hash(precision))
      case _ => Failure(new IllegalArgumentException("Illegal combination of arguments: can only define one of object, point, bounds and hash"))
    }
    triedCommandArgument.map(arg => redisArgs.add(arg.keyword))
  }
}

object GetCommand {
  sealed trait GetCommandArgument {
    val keyword: String
  }

  final object WithFields extends GetCommandArgument {
    override val keyword: String = "WITHFIELDS"
  }

  final object Object extends GetCommandArgument {
    override val keyword: String = "OBJECT"
  }

  final object Point extends GetCommandArgument {
    override val keyword: String = "POINT"
  }

  final object Bounds extends GetCommandArgument {
    override val keyword: String = "BOUNDS"
  }

  final case class Hash(precision: Int) extends GetCommandArgument {
    override val keyword: String = "HASH"
  }
}