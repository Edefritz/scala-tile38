package io.github.edefritz.commands

import io.github.edefritz.commands.GetCommand._
import io.github.edefritz.commands.OutputCommand.OutputCommandArgument
import io.github.edefritz.commands.SetCommand.{
  Ex,
  Fields,
  SetCommandArgument,
  SetCommandFields,
  SetCommandInputFormat,
  SetCondition,
  SetString,
  Bounds => SetBounds,
  Hash => SetHash,
  Object => SetObject,
  Point => SetPoint
}
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.protocol.{ CommandArgs, CommandType, ProtocolKeyword }

import scala.jdk.CollectionConverters._
import scala.util.{ Success, Try }

sealed trait Tile38Command {
  val protocolKeyword: ProtocolKeyword
  def compileArguments(): Try[CommandArgs[String, String]]
}

final case class SetCommand(
    key: String,
    id: String,
    // TODO: How to not make this as an Option so users don't have to pass Some() ???
    inputFormat: SetCommandArgument with SetCommandInputFormat,
    fields: Option[Fields],
    ex: Option[Ex],
    setCondition: Option[SetCommandArgument with SetCondition]
) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = CommandType.SET

  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val args = new CommandArgs(StringCodec.UTF8)
    args.add(key)
    args.add(id)

    fields.foreach { f =>
      f.fields.foreach {
        case (fieldName, fieldValue) => args.add(f.keyword).add(fieldName).add(fieldValue)
      }
    }

    ex.foreach { e =>
      args.add(e.keyword).add(e.seconds)
    }

    setCondition.foreach { n =>
      args.add(n.keyword)
    }

    inputFormat match {
      case SetPoint(lat, lon, z) =>
        args
          .add(inputFormat.keyword)
          .add(lat)
          .add(lon)
        if (z.isDefined) args.add(z.get)
      case SetObject(geojson) => args.add(inputFormat.keyword).add(geojson)
      case SetBounds(minlat, minlon, maxlat, maxlon) =>
        args.add(inputFormat.keyword).add(minlat).add(minlon).add(maxlat).add(maxlon)
      case SetHash(hash)     => args.add(inputFormat.keyword).add(hash)
      case SetString(string) => args.add(inputFormat.keyword).add(string)
      case other             => args.add(other.keyword)
    }
    Success(args)
  }
}

object SetCommand {
  def apply(
      key: String,
      id: String,
      inputFormat: SetCommandArgument with SetCommandInputFormat,
      fields: Map[String, Double] = Map.empty,
      ex: Int = 0,
      setCondition: SetCommandArgument with SetCondition = Default
  ): SetCommand = {
    val fieldsArgument       = if (fields.nonEmpty) Some(Fields(fields)) else None
    val exArgument           = if (ex > 0) Some(Ex(ex)) else None
    val setConditionArgument = Some(setCondition)
    this(key, id, inputFormat, fieldsArgument, exArgument, setConditionArgument)
  }

  sealed trait SetCommandArgument {
    val keyword: String
  }

  sealed trait SetCondition

  final case class NotExists() extends SetCommandArgument with SetCondition {
    override val keyword: String = "NX"
  }

  /**
    * Sets entry only if it already exists
    */
  final case class Exists() extends SetCommandArgument with SetCondition {
    override val keyword: String = "XX"
  }

  // Adding default condition instead of option
  final case class Default() extends SetCommandArgument with SetCondition {
    override val keyword: String = ""
  }

  // Do we need this trait?
  sealed trait SetCommandFields

  final case class Fields(fields: Map[String, Double]) extends SetCommandArgument with SetCommandFields {
    override val keyword: String = "FIELD"
  }

  final case class Ex(seconds: Int) extends SetCommandArgument {
    override val keyword: String = "EX"
  }

  sealed trait SetCommandInputFormat

  final case class Point(lat: Double, lon: Double, z: Option[Double] = None)
      extends SetCommandArgument
      with SetCommandInputFormat {
    override val keyword: String = "POINT"
  }

  final case class Object(geojson: String) extends SetCommandArgument with SetCommandInputFormat {
    override val keyword: String = "OBJECT"
  }

  final case class Bounds(minlat: Double, minlon: Double, maxlat: Double, maxlon: Double)
      extends SetCommandArgument
      with SetCommandInputFormat {
    override val keyword: String = "BOUNDS"
  }

  final case class Hash(hash: String) extends SetCommandArgument with SetCommandInputFormat {
    override val keyword: String = "HASH"
  }
  final case class SetString(string: String) extends SetCommandArgument with SetCommandInputFormat {
    override val keyword: String = "STRING"
  }
}

final case class GetCommand(
    key: String,
    id: String,
    withFields: Boolean = false,
    outputFormat: GetCommandArgument with GetCommandOutputFormat
) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = CommandType.GET
  override def compileArguments(): Try[CommandArgs[String, String]] = {
    val args = new CommandArgs(StringCodec.UTF8)
    args.add(key)
    args.add(id)
    if (withFields) args.add(WithFields.keyword)
    outputFormat match {
      case Hash(precision) => args.add(outputFormat.keyword).add(precision)
      case other           => args.add(other.keyword)
    }
    Success(args)
  }
}

object GetCommand {
  sealed trait GetCommandArgument {
    val keyword: String
  }

  sealed trait GetCommandOutputFormat

  final object WithFields extends GetCommandArgument {
    override val keyword: String = "WITHFIELDS"
  }

  final object Object extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "OBJECT"
  }

  final object Point extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "POINT"
  }

  final object Bounds extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "BOUNDS"
  }

  final case class Hash(precision: Int) extends GetCommandArgument with GetCommandOutputFormat {
    override val keyword: String = "HASH"
  }
}

final case class OutputCommand(format: OutputCommandArgument) extends Tile38Command {
  override val protocolKeyword: ProtocolKeyword = OutputCommandType

  override def compileArguments(): Try[CommandArgs[String, String]] =
    Success(new CommandArgs[String, String](StringCodec.UTF8).add(format.value))
}

object OutputCommand {
  sealed trait OutputCommandArgument {
    val value: String
  }
  final object Json extends OutputCommandArgument {
    override val value: String = "json"
  }
  // We don't support parsing Resp responses
  final object Resp extends OutputCommandArgument {
    override val value: String = "resp"
  }
}
