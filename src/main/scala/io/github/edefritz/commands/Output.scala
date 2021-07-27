package io.github.edefritz.commands

import io.github.edefritz.client.Tile38Client
import io.github.edefritz.customCommandTypes.OutputCommandType
import io.github.edefritz.model.OutputType
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
