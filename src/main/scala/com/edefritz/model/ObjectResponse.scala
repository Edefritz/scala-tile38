package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class ObjectResponse(
    ok: Boolean,
    `object`: GeoJson,
    fields: Option[Map[String, Double]],
    elapsed: String
)

object ObjectResponse {
  lazy implicit val decoder: Decoder[ObjectResponse] = deriveDecoder
}
