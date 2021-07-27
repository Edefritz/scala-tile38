package io.github.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class SuccessfulOperationResponse(
    ok: Boolean,
    elapsed: String
)

object SuccessfulOperationResponse {
  lazy implicit val decoder: Decoder[SuccessfulOperationResponse] =
    deriveDecoder
}
