package io.github.edefritz.responses

import io.circe.generic.semiauto._
import io.circe.{ Codec, Decoder, DecodingFailure, HCursor, Json }

sealed trait Tile38Response {
  val ok: Boolean
  // TODO: we could return a proper type for duration
  val elapsed: String
  // TODO: Consider what to do with these
  val fields: Option[Map[String, Double]] = None
}

object Tile38Response {

  final case class Tile38ResponseDeserializationException(msg: String) extends RuntimeException(msg)

  final implicit val decoder: Decoder[Tile38Response] =
    (cursor: HCursor) => {
      // TODO: Do this nicer
      if (cursor.downField("point").succeeded) cursor.as[PointResponse]
      else if (cursor.downField("hash").succeeded) cursor.as[HashResponse]
      else if (cursor.downField("hashes").succeeded) cursor.as[HashesResponse]
      else if (cursor.downField("bounds").succeeded) cursor.as[BoundsResponse]
      else if (cursor.downField("object").succeeded) cursor.as[ObjectResponse]
      else if (cursor.downField("objects").succeeded) cursor.as[ObjectsResponse]
      else if (cursor.downField("ids").succeeded) cursor.as[IdsResponse]
      else if (cursor.downField("count").succeeded) cursor.as[CountResponse]
      else if (cursor.downField("ttl").succeeded) cursor.as[TimeToLiveResponse]
      else if (cursor.downField("err").succeeded) cursor.as[Tile38ReponseError]
      else if (cursor.downField("ok").succeeded) cursor.as[Tile38SuccessfulResponse]
      else Left(DecodingFailure(s"Cannot determine response type for $cursor", List.empty))
    }

}

final case class Point(lat: Double, lon: Double, z: Option[Double] = None)
object Point {
  implicit val codec: Codec[Point] = deriveCodec
}

final case class TimeToLiveResponse(
    override val ok: Boolean,
    override val elapsed: String,
    ttl: Int
) extends Tile38Response
object TimeToLiveResponse {
  implicit val decoder: Decoder[TimeToLiveResponse] = deriveDecoder
}

final case class PointResponse(
    override val ok: Boolean,
    override val elapsed: String,
    point: Point,
    // TODO: Why does this need an override to be decoded correctly?
    override val fields: Option[Map[String, Double]]
) extends Tile38Response
object PointResponse {
  implicit val decoder: Decoder[PointResponse] = deriveDecoder
}

final case class HashResponse(
    override val ok: Boolean,
    override val elapsed: String,
    hash: String
) extends Tile38Response
object HashResponse {
  implicit val decoder: Decoder[HashResponse] = deriveDecoder
}

final case class BoundsResponse(
    override val ok: Boolean,
    override val elapsed: String,
    bounds: Bounds
) extends Tile38Response
object BoundsResponse {
  implicit val decoder: Decoder[BoundsResponse] = deriveDecoder
}

case class Bounds(sw: LatLon, ne: LatLon)
case class LatLon(lat: Double, lon: Double)
object Bounds {
  lazy implicit val decoder: Decoder[Bounds] = deriveDecoder
}
object LatLon {
  lazy implicit val decoder: Decoder[LatLon] = deriveDecoder
}

case class ObjectResponse(
    override val ok: Boolean,
    override val elapsed: String,
    `object`: Json
) extends Tile38Response
object ObjectResponse {
  lazy implicit val decoder: Decoder[ObjectResponse] = deriveDecoder
}

case class ObjectsResponse(
                           override val ok: Boolean,
                           override val elapsed: String,
                           `objects`: Json
                         ) extends Tile38Response
object ObjectsResponse {
  lazy implicit val decoder: Decoder[ObjectsResponse] = deriveDecoder
}

final case class HashesResponse(
                                 override val ok: Boolean,
                                 override val elapsed: String,
                                 hashes: Json
                               ) extends Tile38Response
object HashesResponse {
  implicit val decoder: Decoder[HashesResponse] = deriveDecoder
}

final case class CountResponse(
    override val ok: Boolean,
    count: Int,
    cursor: Int,
    override val elapsed: String
) extends Tile38Response

object CountResponse {
  implicit val decoder: Decoder[CountResponse] = deriveDecoder
}


final case class IdsResponse(
    override val ok: Boolean,
    ids: List[String],
    count: Int,
    cursor: Int,
    override val elapsed: String
) extends Tile38Response

object IdsResponse {
  implicit val decoder: Decoder[IdsResponse] = deriveDecoder
}

final case class Tile38ReponseError(override val ok: Boolean, override val elapsed: String, err: String)
    extends Tile38Response

object Tile38ReponseError {
  lazy implicit val decoder: Decoder[Tile38ReponseError] = deriveDecoder
}

final case class Tile38SuccessfulResponse(override val ok: Boolean, override val elapsed: String) extends Tile38Response

object Tile38SuccessfulResponse {
  lazy implicit val decoder: Decoder[Tile38SuccessfulResponse] = deriveDecoder
}
