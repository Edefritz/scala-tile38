package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
case class BoundsResponse(
    ok: Boolean,
    bounds: Bounds,
    fields: Option[Map[String, Double]],
    elapsed: String
)
case class Bounds(sw: LatLon, ne: LatLon)
case class LatLon(lat: Double, lon: Double)

object BoundsResponse {
  lazy implicit val decoder: Decoder[BoundsResponse] = deriveDecoder
}

object Bounds {
  lazy implicit val decoder: Decoder[Bounds] = deriveDecoder
}

object LatLon {
  lazy implicit val decoder: Decoder[LatLon] = deriveDecoder
}
