package io.github.edefritz.commands

import io.lettuce.core.protocol.{ CommandArgs, ProtocolKeyword }

import scala.jdk.CollectionConverters._
import scala.util.Try

trait Tile38Command {
  val protocolKeyword: ProtocolKeyword
  def compileArguments(): Try[CommandArgs[String, String]]
}
