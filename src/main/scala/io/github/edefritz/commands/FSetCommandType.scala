package io.github.edefritz.commands

import io.lettuce.core.protocol.ProtocolKeyword

object FSetCommandType extends ProtocolKeyword {
  override def name() = "FSET"

  override def getBytes: Array[Byte] = name().getBytes()
}
