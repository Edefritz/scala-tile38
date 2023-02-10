package io.github.edefritz.commands

import io.github.edefritz.commands.SetCommand.{
  Ex,
  Fields,
  SetCommandArgument,
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

import scala.util.{ Success, Try }

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
  private def create(
      key: String,
      id: String,
      inputFormat: SetCommandArgument with SetCommandInputFormat,
      fields: Option[Fields],
      ex: Option[Ex],
      setCondition: Option[SetCommandArgument with SetCondition]
  ): SetCommand = {
    new SetCommand(key, id, inputFormat, fields, ex, setCondition)
  }

  def apply(
      key: String,
      id: String,
      inputFormat: SetCommandArgument with SetCommandInputFormat,
      fields: Map[String, Double] = Map.empty,
      ex: Int = 0,
      setCondition: Option[SetCommandArgument with SetCondition] = None
  ): SetCommand = {
    val fieldsArgument: Option[Fields] = if (fields.nonEmpty) Some(Fields(fields)) else None
    val exArgument: Option[Ex]         = if (ex > 0) Some(Ex(ex)) else None
    create(key, id, inputFormat, fieldsArgument, exArgument, setCondition)
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

  //  // Adding default condition instead of option
  //  final case class Default() extends SetCommandArgument with SetCondition {
  //    override val keyword: String = ""
  //  }

  // Do we need this trait?
  trait SetCommandFields

  final case class Fields(fields: Map[String, Double]) extends SetCommandArgument with SetCommandFields {
    override val keyword: String = "FIELD"
  }

  final case class Ex(seconds: Int) extends SetCommandArgument {
    override val keyword: String = "EX"
  }

  trait SetCommandInputFormat

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
