package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.auto._

case class Coordinates(x: Double, y: Double)
object Coordinates {
  implicit val decoder: Decoder[Coordinates] =
    Decoder[(Double, Double)].map(p => Coordinates(p._1, p._2))
}

// TODO: this is not a full geojson yet, it only reflects individual features
sealed trait GeoJson
case class GeoJsonPoint(coordinates: Coordinates) extends GeoJson
case class LineString(coordinates: List[Coordinates]) extends GeoJson
case class MultiPoint(coordinates: List[Coordinates]) extends GeoJson
case class MultiLineString(coordinates: List[List[Coordinates]]) extends GeoJson
case class Polygon(coordinates: List[List[Coordinates]]) extends GeoJson
case class MultiPolygon(coordinates: List[List[List[Coordinates]]])
    extends GeoJson
case class GeometryCollection(geometries: List[GeoJson]) extends GeoJson

object GeoJson {
  implicit val decoder: Decoder[GeoJson] = Decoder.instance { c =>
    c.downField("type").as[String].map(_.toLowerCase).flatMap {
      case "point"              => c.as[GeoJsonPoint]
      case "linestring"         => c.as[LineString]
      case "multipoint"         => c.as[MultiPoint]
      case "multilinestring"    => c.as[MultiLineString]
      case "polygon"            => c.as[Polygon]
      case "multipolygon"       => c.as[MultiPolygon]
      case "geometrycollection" => c.as[GeometryCollection]
    }
  }
}