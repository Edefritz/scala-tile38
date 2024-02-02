# scala-tile38 (WIP)

This is an async [tile38](https://tile38.com/) client written in Scala. It abstracts away the complexity of implementing the redis protocol and compose tile38 commands by hand. Instead, you get a convenient API to interact with tile38.

This is a work in progress. Feel free to test and play around with it, but I wouldn't recommend using it in a prod environment.

## Example

```scala
import io.github.edefritz.client.Tile38Client
// configure connection to tile38
val connection               = "redis://localhost:9851"
val client: Tile38Client[IO] = Tile38Client.forAsync[IO](connection)

for {
  _      <- client.exec(SetCommand("fleet", "1", inputFormat = SetCommand.Point(1, 2)))
  result <- client.exec(GetCommand("fleet", "2", withFields = true, outputFormat = GetCommand.Object))
  _      <- IO.delay(print(result))
} yield ExitCode.Success

```

## Supported Commands

- GET
- OUTPUT
- SET
- TTL

## Installation

Add dependency to your build.sbt
```
libraryDependencies += "io.github.edefritz" % "scala-tile38" % "0.1"
```

### Test

```
# Run both unit & integration tests
# needs docker
> make test

# or without docker
> tile38-server
> sbt test
```

### Further development

This project started as a pet project to get a better understanding of Scala. While it works in theory there are some issues that need to be resolved so it becomes useful.

- Coverage of possible commands is like 50%. There are a lot more tha could be added: https://tile38.com/commands
- There is no support for LEADER / FOLLOWER setup
- It would be nice if the pipeline could run the tests publish the artifacts automatically
- For a good reference client library in python: https://github.com/iwpnd/pyle38