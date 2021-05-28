package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class HashResponse(
    ok: Boolean,
    hash: String,
    fields: Option[Map[String, Double]],
    elapsed: String
)

object HashResponse {
  lazy implicit val decoder: Decoder[HashResponse] = deriveDecoder
}
