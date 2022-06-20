package io.github.edefritz.commands

import io.lettuce.core.protocol.ProtocolKeyword

object OutputCommandType extends ProtocolKeyword {
  override def name() = "OUTPUT"

  override def getBytes: Array[Byte] = name().getBytes()
}
