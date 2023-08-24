scalaVersion := "2.13.11"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"

libraryDependencies += "io.mikael" % "urlbuilder" % "2.0.9"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.2"

val circeVersion     = "0.14.5"
val sttClientVersion = "3.9.0"

libraryDependencies += "com.softwaremill.sttp.client3" %% "circe"         % sttClientVersion
libraryDependencies += "io.circe"                      %% "circe-core"    % circeVersion
libraryDependencies += "io.circe"                      %% "circe-generic" % circeVersion
libraryDependencies += "io.circe"                      %% "circe-parser"  % circeVersion

libraryDependencies += "joda-time" % "joda-time" % "2.12.5"

val PekkoVersion = "1.0.1"
libraryDependencies += "org.apache.pekko" %% "pekko-slf4j"  % PekkoVersion % Provided

Test / parallelExecution := false

libraryDependencies += "com.softwaremill.sttp.client3" %% "pekko-http-backend" % sttClientVersion % Test
libraryDependencies += "com.softwaremill.sttp.client3" %% "akka-http-backend"  % sttClientVersion % Test
libraryDependencies += "com.softwaremill.sttp.client3" %% "okhttp-backend"     % sttClientVersion % Test

libraryDependencies += "org.apache.pekko" %% "pekko-stream" % PekkoVersion % Test

val AkkaVersion = "2.6.21" // 2.6.XX latest version under Apache 2.0
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion % Test

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
