scalaVersion := "2.13.10"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"

val sttClientVersion = "3.8.3"

libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % sttClientVersion

libraryDependencies += "com.softwaremill.sttp.client3" %% "akka-http-backend" % sttClientVersion % Test

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
