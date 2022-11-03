scalaVersion := "2.13.10"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"

libraryDependencies += "io.mikael" % "urlbuilder" % "2.0.9"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1"

val circeVersion     = "0.14.3"
val sttClientVersion = "3.8.2"

libraryDependencies += "com.softwaremill.sttp.client3" %% "circe"         % sttClientVersion
libraryDependencies += "io.circe"                      %% "circe-core"    % circeVersion
libraryDependencies += "io.circe"                      %% "circe-generic" % circeVersion
libraryDependencies += "io.circe"                      %% "circe-parser"  % circeVersion

libraryDependencies += "joda-time" % "joda-time" % "2.12.1"

val AkkaVersion = "2.6.20" // 2.6.20 latest version under Apache 2.0

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion % Provided

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion % Test

libraryDependencies += "com.softwaremill.sttp.client3" %% "akka-http-backend" % sttClientVersion % Test
libraryDependencies += "com.softwaremill.sttp.client3" %% "okhttp-backend"    % sttClientVersion % Test

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
