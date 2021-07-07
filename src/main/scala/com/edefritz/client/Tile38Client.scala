package com.edefritz.client

import com.edefritz.commands.{Bounds, Drop, Get, Output, Scan, Set}
import com.edefritz.model.{JsonType, OutputType, RespType}
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.{CommandArgs, ProtocolKeyword}

import scala.concurrent.Future
import scala.jdk.FutureConverters.CompletionStageOps

class Tile38Client(connectionString: String) {
  lazy val codec: StringCodec = StringCodec.UTF8;
  lazy val client: RedisClient = RedisClient.create(connectionString)
  lazy val connection: StatefulRedisConnection[String, String] =
    client.connect(codec)
  lazy val sync: RedisCommands[String, String] = connection.sync()
  lazy val async: RedisAsyncCommands[String, String] = connection.async()
  // By default a lettuce connection would return RESP
  private var format: OutputType = RespType

  // This is needed to configure the connection to request JSON from Tile38
  private def forceJson() = {
    if (this.format != JsonType) {
      format = JsonType
      Output(JsonType)(this).exec()
    }
  }

  def get(key: String, id: String): Get = {
    forceJson()
    Get(key, id)(this)
  }

  def set(key: String, id: String): Set = {
    forceJson()
    Set(key, id)(this)
  }

  def scan(key: String): Scan = {
    forceJson()
    Scan(key)(this)
  }

  def bounds(key: String): Bounds = {
    forceJson()
    Bounds(key)(this)
  }

  /**
    * Remove all objects from specified key.
    *
    *
    */
  def drop(key: String): Drop = {
    forceJson()
    Drop(key)(this)
  }

  def dispatch(
      commandType: ProtocolKeyword,
      args: CommandArgs[String, String]
  ): String = {
    sync.dispatch(commandType, new ValueOutput(codec), args)
  }

  def dispatchAsync(
      commandType: ProtocolKeyword,
      args: CommandArgs[String, String]
  ): Future[String] = {
    async.dispatch(commandType, new ValueOutput(codec), args).asScala
  }

  def close() = {
    connection.close()
  }

}
