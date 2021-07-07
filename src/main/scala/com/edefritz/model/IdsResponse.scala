package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class IdsResponse(
    ok: Boolean,
    ids: Seq[String],
    count: Int,
    cursor: Int,
    elapsed: String
)

object IdsResponse {
  lazy implicit val decoder: Decoder[IdsResponse] = deriveDecoder
}
