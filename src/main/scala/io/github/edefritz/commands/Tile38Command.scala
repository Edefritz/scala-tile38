package io.github.edefritz.commands

import io.github.edefritz.errors._
import io.lettuce.core.protocol.ProtocolKeyword

trait Tile38Command {
  val protocolKeyword: ProtocolKeyword
  val arguments: Seq[Tile38CommandArguments]
}
