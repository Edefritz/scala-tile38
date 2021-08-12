package io.github.edefritz.commands

import io.circe.parser
import io.github.edefritz.client.Tile38Client
import io.github.edefritz.errors.Tile38Error
import io.github.edefritz.model.{BaseResponse, SuccessfulOperationResponse}
import io.lettuce.core.protocol.CommandType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO: This whole approach of mutable variables that are overwritten is somewhat bad
case class Set(key: String, id: String)(implicit tile38Client: Tile38Client)
    extends Tile38Command {

  private var _args: Seq[Any] = Seq[Any](key, id)
  private var _fields: Map[String, Double] = Map[String, Double]()
  private var _input: Seq[Any] = Seq[Any]()
  private var _ex: Seq[Any] = Seq[Any]()
  private var _xx: Boolean = false
  private var _nx: Boolean = false

  def point(lat: Double, lon: Double, z: Option[Double] = None): Set = {
    var args = Seq("POINT", lat, lon)
    if (z.isDefined) args = args :+ z.get
    this._input = args
    this
  }

  def geojson(geojson: String): Set = {
    this._input = Seq("OBJECT", geojson)
    this
  }

  def bounds(
      minlat: Double,
      minlon: Double,
      maxlat: Double,
      maxlon: Double
  ): Set = {
    this._input = Seq("BOUNDS", minlat, minlon, maxlat, maxlon)
    this
  }

  def hash(hash: String): Set = {
    this._input = Seq("HASH", hash)
    this
  }

  def string(value: String): Set = {
    this._input = Seq("STRING", value)
    this
  }

  def withFields(fields: Map[String, Double]): Set = {
    this._fields = fields
    this
  }

  def ex(seconds: Int): Set = {
    this._ex = Seq("EX", seconds)
    this
  }

  def xx(): Set = {
    this._xx = true
    this
  }

  def nx(): Set = {
    this._nx = true
    this
  }

  private def unpackFields(): Unit = {
    _fields.foreach(f => {
      _args = _args ++ Seq("FIELD", f._1, f._2)
    })
  }

  private def unpackInput(): Unit = {
    _args = _args ++ _input
  }

  private def unpackExpiration(): Unit = {
    if (_ex.nonEmpty) {
      _args = _args ++ _ex
    }
  }

  private def unpackXX(): Unit = {
    if (_xx) _args = _args :+ "XX"
  }

  private def unpackNX(): Unit = {
    if (_nx) _args = _args :+ "NX"
  }

  // compose new seq here instead of returning modified one here
  def compileArgs() = {
    unpackFields()
    unpackExpiration()
    unpackXX()
    unpackNX()
    unpackInput()
    _args
  }

  def exec(): Future[Either[Tile38Error, SuccessfulOperationResponse]] = {
    val response = super.execAsync(CommandType.SET, compileArgs())(tile38Client)
    response.map(r => {
      parser.decode[BaseResponse](r) match {
        case Left(_) =>
          Left(super.decodeTile38Error(r))
        case Right(objectResponse: SuccessfulOperationResponse) =>
          Right(objectResponse)
      }
    })
  }
}
