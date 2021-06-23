package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class KeyBoundsResponse(
    ok: Boolean,
    bounds: Polygon,
    elapsed: String
)

object KeyBoundsResponse {
  lazy implicit val decoder: Decoder[KeyBoundsResponse] = deriveDecoder
}
