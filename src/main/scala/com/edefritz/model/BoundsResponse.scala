package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
case class BoundsResponse(
    ok: Boolean,
    bounds: Bounds,
    fields: Option[Map[String, Double]],
    elapsed: String
)

case class SeqBoundsResponse(
    ok: Boolean,
    bounds: Seq[IdBounds],
    count: Int,
    cursor: Int,
    elapsed: String
)
case class IdBounds(id: String, bounds: Bounds)
case class Bounds(sw: LatLon, ne: LatLon)
case class LatLon(lat: Double, lon: Double)

object SeqBoundsResponse {
  lazy implicit val decoder: Decoder[SeqBoundsResponse] = deriveDecoder
}

object IdBounds {
  lazy implicit val decoder: Decoder[IdBounds] = deriveDecoder
}

object BoundsResponse {
  lazy implicit val decoder: Decoder[BoundsResponse] = deriveDecoder
}

object Bounds {
  lazy implicit val decoder: Decoder[Bounds] = deriveDecoder
}

object LatLon {
  lazy implicit val decoder: Decoder[LatLon] = deriveDecoder
}
