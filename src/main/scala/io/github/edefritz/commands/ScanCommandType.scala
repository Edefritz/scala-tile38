package io.github.edefritz.commands

import io.lettuce.core.protocol.ProtocolKeyword

object ScanCommandType extends ProtocolKeyword {
  override def name() = "SCAN"

  override def getBytes: Array[Byte] = name().getBytes()

}
