package de.iwelt.quest4s

import sttp.client3.SttpBackend

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

abstract class AbstractQuerySuite extends BaseSuite {

  val backend: SttpBackend[Future, _]

  override def beforeAll(): Unit = {}
  override def afterAll(): Unit  = {}

  test(s"Create Table") {
    val sqlQuery =
      s"CREATE TABLE IF NOT EXISTS $table(date TIMESTAMP, instrument SYMBOL, quantity Long, price Double, side String) timestamp(date) PARTITION BY DAY"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))
  }

  test(s"Insert Data") {
    val sqlQuery        = s"INSERT INTO $table VALUES('2021-10-05T11:31:35.878Z', 'AAPL', 255, 123.33, 'B');"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)

    assertEquals(executionResult.get("ddl"), Some("OK"))

    val sqlQueryML = s"INSERT INTO $table VALUES('2021-10-05T11:31:35.878Z', 'AAPL', 245, 123.4, 'C'), " +
      s"('2021-10-05T12:31:35.878Z', 'AAPL', 245, 123.3, 'C'), " +
      s"('2021-10-05T13:31:35.878Z', 'AAPL', 250, 123.1, 'C'), " +
      s"('2021-10-05T15:31:35.878Z', 'AAPL', 265, 128.1, 'A'), " +
      s"('2021-10-05T13:32:35.878Z', 'AAPL', 250, 123.1, 'C'), " +
      s"('2021-10-05T14:31:35.878Z', 'AAPL', 250, 123.0, 'C');"

    val executionResultML = questDbClient.executeSql(sqlQueryML, 60.seconds)
    assertEquals(executionResultML.get("ddl"), Some("OK"))
  }

  test(s"Update Data") {
    val sqlQuery        = s"UPDATE $table SET price = 135.12 WHERE instrument = 'AAPL' and side = 'A';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))
    val countUpdated = executionResult.get("updated").map(_.toString.toLong).getOrElse(0L)
    assertEquals(countUpdated >= 1, true)
  }

  test(s"Select Data") {
    val sqlQuery        = s"Select * from $table WHERE instrument = 'AAPL' and side = 'C';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("query"), Some(sqlQuery))
    assertEquals(executionResult.contains("columns"), true)
    assertEquals(executionResult.contains("dataset"), true)
    assertEquals(executionResult.get("count"), Some(5))
  }

  test(s"Drop Table") {
    val sqlQuery        = s"DROP TABLE '$table';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))
  }

}
