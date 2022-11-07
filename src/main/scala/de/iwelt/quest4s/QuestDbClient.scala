package de.iwelt.quest4s

import better.files.File
import com.typesafe.config
import com.typesafe.config.ConfigFactory
import de.iwelt.quest4s.converter.CirceSchema
import de.iwelt.quest4s.exception.{ Quest4SInvalidRequestException, Quest4SParseException }
import de.iwelt.quest4s.util.UrlHelper
import io.circe.Error
import io.circe.generic.AutoDerivation
import sttp.client3.circe.SttpCirceApi
import sttp.client3.{ SttpBackend, _ }
import sttp.model.Method

import java.io
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

case class QuestDbClient(host: String, backend: SttpBackend[Future, _], username: String = "", password: String = "")
    extends UrlHelper
    with SttpCirceApi
    with AutoDerivation
    with CirceSchema {

  def executeSqlRequest(
      sql: String,
      additionalParameters: Map[String, Any] = Map()
  ): RequestT[Identity, Either[sttp.client3.ResponseException[String, Error], Map[String, Any]], Any] = {
    val urlString = buildRequestUrl(s"$host/exec", additionalParameters ++ Map("query" -> sql))
    basicRequest
      .method(Method.GET, uri"$urlString")
      .auth
      .basic(username, password)
      .response(asJson[Map[String, Any]])
  }

  def importCsvRequest(
      tableName: String,
      file: File,
      additionalParameters: Map[String, Any] = Map()
  ): RequestT[Identity, Either[sttp.client3.ResponseException[String, Error], Map[String, Any]], Any] = {
    val urlString = buildRequestUrl(s"$host/imp", additionalParameters ++ Map("fmt" -> "json", "name" -> tableName))
    basicRequest
      .method(Method.PUT, uri"$urlString")
      .contentType("multipart/form-data")
      .auth
      .basic(username, password)
      .multipartBody(
        Seq(
          multipartFile("data", file.toJava)
        )
      )
      .response(asJson[Map[String, Any]])
  }

  def exportCsvRequest(query: String, limit: Option[String] = None): RequestT[Identity, Either[String, io.File], Any] = {
    val urlString = buildRequestUrl(s"$host/exp", Map("query" -> query, "limit" -> limit))
    basicRequest
      .method(Method.GET, uri"$urlString")
      .auth
      .basic(username, password)
      .response(asFile(File.temporaryFile(suffix = ".csv").get().toJava))
  }

  def executeSql(
      sql: String,
      maxWaitDuration: Duration = QuestDbClient.defaultMaxWaitTime,
      additionalParameters: Map[String, Any] = Map()
  ): Map[String, Any] = {
    val resultFuture   = backend.send(executeSqlRequest(sql, additionalParameters))
    val responseResult = Await.result(resultFuture, maxWaitDuration)
    responseResult.body.getOrElse(
      if (responseResult.code.isSuccess) {
        throw new Quest4SParseException("could not parse body")
      }
      else {
        throw new Quest4SInvalidRequestException(responseResult.body.left.get)
      }
    )
  }

  def importCsv(
      tableName: String,
      file: File,
      maxWaitDuration: Duration = QuestDbClient.defaultMaxWaitTime,
      additionalParameters: Map[String, Any] = Map()
  ): Map[String, Any] = {
    // case class could not be used at this moment because server sometime turn "table response" instead of json https://github.com/questdb/questdb/issues/2703
    val resultFuture   = backend.send(importCsvRequest(tableName, file, additionalParameters))
    val responseResult = Await.result(resultFuture, maxWaitDuration)
    responseResult.body.getOrElse(
      if (responseResult.code.isSuccess) {
        Map()
      }
      else {
        throw new Quest4SInvalidRequestException(responseResult.body.left.get)
      }
    )
  }

  def exportCsv(query: String, maxWaitDuration: Duration = QuestDbClient.defaultMaxWaitTime, limit: Option[String] = None): File = {
    val resultFuture   = backend.send(exportCsvRequest(query, limit))
    val responseResult = Await.result(resultFuture, maxWaitDuration)
    val javaFile = responseResult.body.getOrElse(
      throw new Quest4SParseException("could not parse body")
    )
    File(javaFile.toURI)
  }

}

object QuestDbClient {
  lazy val defaultMaxWaitTime: Duration = {
    val conf: config.Config = ConfigFactory.load()
    Duration.fromNanos(conf.getDuration("de.iwelt.quest4s.wait.time.max").toNanos)
  }
}
