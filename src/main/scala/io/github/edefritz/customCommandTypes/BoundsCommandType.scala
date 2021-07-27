package io.github.edefritz.customCommandTypes

import io.lettuce.core.protocol.ProtocolKeyword

object BoundsCommandType extends ProtocolKeyword {
  override def name() = "BOUNDS"

  override def getBytes: Array[Byte] = name().getBytes()
}
