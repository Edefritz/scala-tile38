# scala-tile38 (WIP)

This is an async [tile38](https://tile38.com/) client written in Scala. It abstracts away the complexity of implementing the redis protocol and compose tile38 commands by hand. Instead, you get a convenient API to interact with tile38.

## Example

```scala
// configure connection to tile38
val client = new Tile38Client("redis://localhost:9851")

// set item
client.set("fleet", "1").point(1,2).exec()

// retrieve item in various ways
val obj = client.get("fleet", "1").asObject()
val point = client.get("fleet", "1").asPoint()
val bounds = client.get("fleet", "1").asBounds()

client.close()
```

## Supported Commands

- BOUNDS
- DROP
- GET
- OUTPUT
- SCAN
- SET
- FSET

## Installation

Add dependency to your build.sbt
```
libraryDependencies += "io.github.edefritz" % "scala-tile38" % "0.1"
```

Use in your app

```scala
import io.github.edefritz.client.Tile38ClientImpl

val client = new Tile38ClientImpl("redis://localhost:9851")

val set = Await.result(client.set("my_key", "1").point(1, 2).exec(), Duration.Inf)
val get = Await.result(client.get("my_key", "1").asObject(), Duration.Inf)

client.close()
```

### Test

```
# Run both unit & integration tests
docker-compose up
sbt test
```

### Further development

This project started as a pet project to get a better understanding of Scala. While it works in theory there are some issues that need to be resolved so it becomes useful.

- Figure out a proper return type for responses. Right now it's parsing the responses with circe and returns classes. Is this desirable or should we rather return Future[String] and let users parse the response? This might be easier, since sometimes it's hard to get the parsing right for stuff like geojson properties.
- Right now the tile38 commands are built as classes. Depending on the arguments, the class is returned and modified multiple times. Maybe there is a way to compose commands in a more immutable way?
- Examine if we're following Scala best practices (spoiler: we're not)
- Coverage of possible commands is like 50%. There are a lot more tha could be added: https://tile38.com/commands
- There is no support for LEADER / FOLLOWER setup
- It would be nice if the pipeline could run the tests publish the artifacts automatically
- For a good reference client library in python: https://github.com/iwpnd/pyle38