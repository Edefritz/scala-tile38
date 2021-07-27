package io.github.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class HashResponse(
    ok: Boolean,
    hash: String,
    fields: Option[Map[String, Double]],
    elapsed: String
)

case class HashesResponse(
    ok: Boolean,
    hashes: Seq[IdHash],
    count: Int,
    cursor: Int,
    elapsed: String
)
case class IdHash(id: String, hash: String)

object HashesResponse {
  lazy implicit val decoder: Decoder[HashesResponse] = deriveDecoder
}
object IdHash {
  lazy implicit val decoder: Decoder[IdHash] = deriveDecoder
}
object HashResponse {
  lazy implicit val decoder: Decoder[HashResponse] = deriveDecoder
}
