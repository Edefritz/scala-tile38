package io.github.edefritz.responses

import io.circe.Decoder.Result
import io.circe.generic.semiauto._
import io.circe.{ Codec, Decoder, DecodingFailure, HCursor }

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
      if (cursor.downField("point").succeeded) cursor.as[PointResponse]
      else Left(DecodingFailure(s"Cannot determine response type for $cursor", List.empty))
      // TODO: Do this nicer
      //val maybeObject = cursor.downField("object").focus
      //val maybeBounds = cursor.downField("bounds").focus
      //val maybeHash = cursor.downField("hash").focus
    }

}

final case class Point(lat: Double, lon: Double)
object Point {
  implicit val codec: Codec[Point] = deriveCodec
}

final case class PointResponse(
    override val ok: Boolean,
    override val elapsed: String,
    point: Point
) extends Tile38Response
object PointResponse {
  implicit val decoder: Decoder[PointResponse] = deriveDecoder
}
