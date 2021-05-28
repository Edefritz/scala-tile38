package com.edefritz.model

sealed trait OutputType {
  def name: String
}
case object JsonType extends OutputType {
  val name = "JSON"
}

case object RespType extends OutputType {
  val name = "RESP"
}
