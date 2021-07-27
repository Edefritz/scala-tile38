package io.github.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class PointResponse(
    ok: Boolean,
    point: Point,
    fields: Option[Map[String, Double]],
    elapsed: String
)
case class PointsResponse(
    ok: Boolean,
    points: Seq[IdPoint],
    count: Int,
    cursor: Int,
    elapsed: String
)
case class Point(lat: Double, lon: Double)
case class IdPoint(id: String, point: Point)

object PointResponse {
  lazy implicit val decoder: Decoder[PointResponse] = deriveDecoder
}
object PointsResponse {
  lazy implicit val decoder: Decoder[PointsResponse] = deriveDecoder
}
object IdPoint {
  lazy implicit val decoder: Decoder[IdPoint] = deriveDecoder
}
object Point {
  lazy implicit val decoder: Decoder[Point] = deriveDecoder
}
