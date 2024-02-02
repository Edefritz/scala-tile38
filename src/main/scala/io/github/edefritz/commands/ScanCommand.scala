package io.github.edefritz.commands

import io.github.edefritz.commands.ScanCommand.{
  Cursor,
  Limit,
  MatchExpression,
  ScanCommandArgument,
  ScanCommandOutputFormat,
  SortOrder
}
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{ CommandArgs, ProtocolKeyword }

import scala.util.{ Success, Try }

final case class ScanCommand(
    key: String,
    cursor: Option[Cursor] = None,
    limit: Option[Limit] = None,
    matchExpression: Option[MatchExpression] = None,
    outputFormat: Option[ScanCommandArgument with ScanCommandOutputFormat] = None,
    sort: Option[ScanCommandArgument with SortOrder] = None
) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = ScanCommandType
  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val args = new CommandArgs(StringCodec.UTF8)
    args.add(key)

    cursor.foreach { c =>
      args.add(c.keyword).add(c.cursor)
    }

    limit.foreach { l =>
      args.add(l.keyword).add(l.limit)
    }

    matchExpression.foreach { m =>
      args.add(m.keyword).add(m.matchExpression)
    }

    sort.foreach { s =>
      args.add(s.keyword)
    }

    outputFormat.foreach { o =>
      args.add(o.keyword)
    }

    Success(args)
  }
}

object ScanCommand {

  sealed trait ScanCommandArgument {
    val keyword: String
  }

  final case class Cursor(cursor: Int) extends ScanCommandArgument {
    override val keyword: String = "CURSOR"
  }

  final case class Limit(limit: Int) extends ScanCommandArgument {
    override val keyword: String = "LIMIT"
  }

  final case class MatchExpression(matchExpression: String) extends ScanCommandArgument {
    override val keyword: String = "MATCH"
  }

  sealed trait SortOrder

  final case class Ascending() extends ScanCommandArgument with SortOrder {
    override val keyword: String = "ASC"
  }

  final case class Descending() extends ScanCommandArgument with SortOrder {
    override val keyword: String = "DESC"
  }

  sealed trait ScanCommandOutputFormat

  final object Count extends ScanCommandArgument with ScanCommandOutputFormat {
    override val keyword: String = "COUNT"
  }

  final object Ids extends ScanCommandArgument with ScanCommandOutputFormat {
    override val keyword: String = "IDS"
  }

  final object Objects extends ScanCommandArgument with ScanCommandOutputFormat {
    override val keyword: String = "OBJECTS"
  }

  final object Points extends ScanCommandArgument with ScanCommandOutputFormat {
    override val keyword: String = "POINTS"
  }

  final object Bounds extends ScanCommandArgument with ScanCommandOutputFormat {
    override val keyword: String = "BOUNDS"
  }

  final case class Hashes(precision: Int) extends ScanCommandArgument with ScanCommandOutputFormat {
    override val keyword: String = "HASHES"
  }

}
