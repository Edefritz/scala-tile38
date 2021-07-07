package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class CountResponse(
    ok: Boolean,
    count: Int,
    cursor: Int,
    elapsed: String
)

object CountResponse {
  lazy implicit val decoder: Decoder[CountResponse] = deriveDecoder
}
