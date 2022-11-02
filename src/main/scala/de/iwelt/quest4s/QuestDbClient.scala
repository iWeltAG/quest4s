package de.iwelt.quest4s

import better.files.File
import de.iwelt.quest4s.exception.Quest4SParseException
import de.iwelt.quest4s.util.UrlHelper
import io.circe.generic.AutoDerivation
import sttp.client3.circe.SttpCirceApi
import sttp.client3.{SttpBackend, _}
import sttp.model.Method

import java.io
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class QuestDbClient(host: String, backend: SttpBackend[Future, _], username: String = "", password: String = "")
    extends UrlHelper
    with SttpCirceApi
    with AutoDerivation {

  def executeSqlRequest(sql: String, additionalParameters: Map[String, Any] = Map()): RequestT[Identity, Either[String, Map[String, Any]], Any] = {
    val urlString = buildRequestUrl(s"$host/exec", additionalParameters ++ Map("query" -> sql))
    basicRequest
      .method(Method.GET, uri"$urlString")
      .auth
      .basic(username, password)
      .response(asJson[Map[String, Any]])
  }

  def importCsvRequest(tableName: String, file: File, additionalParameters: Map[String, Any] = Map()): RequestT[Identity, Either[String, String], Any] = {
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
      .response(asString("UTF-8"))

  }

  def exportCsvRequest(query: String, limit: Option[String] = None): RequestT[Identity, Either[String, io.File], Any] = {
    val urlString = buildRequestUrl(s"$host/exp", Map("query" -> query, "limit" -> limit))
    basicRequest
      .method(Method.GET, uri"$urlString")
      .auth
      .basic(username, password)
      .response(asFile(File.temporaryFile(suffix = ".csv").get().toJava))
  }

  def executeSql(sql: String, maxWaitDuration: Duration, additionalParameters: Map[String, Any] = Map()): Map[String, Any] = {
    val resultFuture   = backend.send(executeSqlRequest(sql, additionalParameters))
    val responseResult = Await.result(resultFuture, maxWaitDuration)
    responseResult.body.getOrElse(throw new Quest4SParseException("could not parse body"))
  }

  def importCsv(tableName: String, file: File, maxWaitDuration: Duration, additionalParameters: Map[String, Any] = Map()): String = {
    val resultFuture   = backend.send(importCsvRequest(tableName, file, additionalParameters))
    val responseResult = Await.result(resultFuture, maxWaitDuration)
    responseResult.body.getOrElse(throw new Quest4SParseException("could not parse body"))
  }

  def exportCsvRequest(query: String, maxWaitDuration: Duration, limit: Option[String] = None): File = {
    val resultFuture   = backend.send(exportCsvRequest(query, limit))
    val responseResult = Await.result(resultFuture, maxWaitDuration)
    val javaFile       = responseResult.body.getOrElse(throw new Quest4SParseException("could not parse body"))
    File(javaFile.toURI)
  }

}
