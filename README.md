# scala-tile38

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

## Installation

TB

## Development

### Test

```
# Run both unit & integration tests
docker-compose up
sbt test
```