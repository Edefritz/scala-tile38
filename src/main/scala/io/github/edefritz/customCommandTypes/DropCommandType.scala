package io.github.edefritz.customCommandTypes

import io.lettuce.core.protocol.ProtocolKeyword

object DropCommandType extends ProtocolKeyword {
  override def name() = "DROP"

  override def getBytes: Array[Byte] = name().getBytes()
}
