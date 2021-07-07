package com.edefritz.model

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class ObjectResponse(
    ok: Boolean,
    `object`: GeoJson,
    fields: Option[Map[String, Double]],
    elapsed: String
)

case class ObjectsResponse(
    ok: Boolean,
    objects: Seq[IdObject],
    count: Int,
    cursor: Int,
    elapsed: String
)

case class IdObject(id: String, `object`: GeoJson)

object ObjectsResponse {
  lazy implicit val decoder: Decoder[ObjectsResponse] = deriveDecoder
}

object IdObject {
  lazy implicit val decoder: Decoder[IdObject] = deriveDecoder
}

object ObjectResponse {
  lazy implicit val decoder: Decoder[ObjectResponse] = deriveDecoder
}
