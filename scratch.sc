
import io.github.edefritz.client.Tile38Client

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

val connection = "redis://localhost:9851"

val client = new Tile38Client(connection)
val set = client.set("fleet", "1").point(1,2).exec()
//val a = Await.result(client.scan("fleet").asPoints(), 2.seconds)
val b = Await.result(client.fset("fleet", "1", Map("c" -> 1.0)).exec(), 2.seconds)

client.close()
//
//val pointSet = client
//  .set("fleet", "2")
//  .withFields(Map("myField" -> 111, "myOtherField" -> 222))
//  .ex(100)
//  .point(1,2)
//  .nx()
//  .exec()
//
//val boundsSet = client
//  .set("fleet", "2")
//  .bounds(1,2,1,2)
//  .exec()
//
//val hashSet = client
//  .set("fleet", "2")
//  .hash("u32hb")
//  .exec()
//
//val stringSet = client
//  .set("fleet", "2")
//  .string("asd")
//  .exec()
//
//
//val objectSet = client
//  .set("fleet", "2")
//  .withFields(Map("myField" -> 1111))
//  .ex(10)
//  .geojson(
//    "{\"type\": \"Feature\", \"properties\": {}, \"geometry\": {\"type\": \"Point\", \"coordinates\": [11.25, 52.9]}}"
//)
//  .exec()
//
//
//
//val obj = client.get("fleet", "2").withFields().asPoint()
//val hash = client.get("fleet", "2").withFields().asHash(5)
//
//val obj2 = client.get("fleet", "2").asObject()
//val point = client.get("fleet", "2").asPoint()
//val bounds = client.get("fleet", "2").asBounds()
//
//bounds.onComplete {
//    case Success(t) => println(t)
//    case Failure(e) => println(e)
//}
//
//val fields = client.get("fleet", "2").withFields().asObject()
//
//val errorHash = client.get("flee", "2").withFields().asHash(5)
//
//client.close()