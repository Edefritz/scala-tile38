package com.edefritz.model
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class PointResponse(ok: Boolean, point: Point, fields: Option[Map[String, Double]], elapsed: String)
object PointResponse {
  lazy implicit val decoder: Decoder[PointResponse] = deriveDecoder
}

case class Point(lat: Double, lon: Double)
object Point {
  lazy implicit val decoder: Decoder[Point] = deriveDecoder
}