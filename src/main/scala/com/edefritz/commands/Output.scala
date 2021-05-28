package com.edefritz.commands

import com.edefritz.client.Tile38Client
import com.edefritz.customCommandTypes.OutputCommandType
import com.edefritz.model.OutputType
import io.lettuce.core.codec.StringCodec

case class Output(outputType: OutputType)(implicit tile38Client: Tile38Client)
    extends Tile38Command {
  val codec = StringCodec.UTF8;
  val args = Seq(outputType.name)
  val commandType = OutputCommandType

  def exec(): String = {
    super.exec(commandType, args)
  }

}
