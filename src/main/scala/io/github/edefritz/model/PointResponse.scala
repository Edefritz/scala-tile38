package io.github.edefritz.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

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
  lazy implicit val encoder: Encoder[PointResponse] = deriveEncoder
}
object PointsResponse {
  lazy implicit val decoder: Decoder[PointsResponse] = deriveDecoder
  lazy implicit val encoder: Encoder[PointsResponse] = deriveEncoder
}
object IdPoint {
  lazy implicit val decoder: Decoder[IdPoint] = deriveDecoder
  lazy implicit val encoder: Encoder[IdPoint] = deriveEncoder
}
object Point {
  lazy implicit val decoder: Decoder[Point] = deriveDecoder
  lazy implicit val encoder: Encoder[Point] = deriveEncoder
}
