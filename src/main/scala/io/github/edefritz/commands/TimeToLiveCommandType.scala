package io.github.edefritz.commands

import io.lettuce.core.protocol.ProtocolKeyword

object TimeToLiveCommandType extends ProtocolKeyword {
  override def name() = "TTL"

  override def getBytes: Array[Byte] = name().getBytes()
}
