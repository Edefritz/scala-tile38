package io.github.edefritz.errors

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

sealed trait Tile38Error

case class Tile38IdNotFoundError(ok: Boolean, err: String, elapsed: String)
    extends Tile38Error
case class Tile38KeyNotFoundError(ok: Boolean, err: String, elapsed: String)
    extends Tile38Error
case class Tile38ResponseDecodingError(err: String) extends Tile38Error
case class Tile38GenericError(ok: Boolean, err: String, elapsed: String)

object Tile38GenericError {
  lazy implicit val decoder: Decoder[Tile38GenericError] = deriveDecoder
}
