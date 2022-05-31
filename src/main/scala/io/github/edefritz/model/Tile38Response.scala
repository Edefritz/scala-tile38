package io.github.edefritz.model

trait Tile38Response {
  val ok: Boolean
  // TODO: we could return a proper type for duration
  val elapsed: String
}
