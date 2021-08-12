package io.github.edefritz.model

import io.circe.{Decoder, DecodingFailure}
import io.circe.generic.semiauto.deriveDecoder

sealed trait BaseResponse
case class SuccessfulOperationResponse(
    ok: Boolean,
    elapsed: String
) extends BaseResponse

object SuccessfulOperationResponse {
  lazy implicit val decoder: Decoder[SuccessfulOperationResponse] =
    deriveDecoder
}

object BaseResponse {
  // TODO: While this works, it think we could save some boilerplate if we decode errors here as well
  implicit val decoder: Decoder[BaseResponse] = Decoder.instance { c =>
    c.downField("ok").as[Boolean].flatMap {
      case true => c.as[SuccessfulOperationResponse]
      case false =>
        Left(
          DecodingFailure(
            "Response is not successful and should be handled separatly",
            List()
          )
        )
    }
  }
}
