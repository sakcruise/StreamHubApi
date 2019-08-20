# StreamHubApi Assignment

## Scala versions and dependencies:
name := "StreamHubApi"

### Version:
```
scalaVersion := "2.12.8"

akkaVersion = "2.5.24"

akkaHttpVersion = "10.1.9"
```
### Dependencies:
```
"com.typesafe.akka" %% "akka-actor" % akkaVersion,

"com.typesafe.akka" %% "akka-testkit" % akkaVersion,

"com.typesafe.akka" %% "akka-stream"  % akkaVersion,

"com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion % Test,

"com.typesafe.akka" %% "akka-http"  % akkaHttpVersion,

"com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

"com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
```

## Using this project

You should be able to import this project into IntelliJ-IDEA or any other Scala
IDE or editor without issue.

Project structure:
src-main-scala
- streamhub     - package
  - StartServer - Contains the main class to start the server at localhost:8080
  - UserReportActor - Actor class which receives messages as GetUniqueUsers(params)
  - UserReportRoutes - trait which contains the Routes details of the api
  
To compile-and-run:

```
sbt run StartServer
```

This should look like:
```
[info] Running streamhub.StartServer
Server connected at http://127.0.0.1:8080/
```

To run tests:

```
sbt test UserReportRoutesSpec
```

This should look like:

```
 UserReportRoutesSpec:
[info] UserReportRoutes1
[info] - should return OK (GET /reports)
[info] UserReportRoutes2
[info] - should return Forbidden (GET /reports)
```
