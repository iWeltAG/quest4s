package de.iwelt.quest4s
import munit.FunSuite
import sttp.client3.SttpBackend

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

abstract class BaseSuite extends FunSuite {
  protected def waitForQuestDbExecution(): Unit = {
    Thread.sleep(1000)
  }

  val backend: SttpBackend[Future, _]

  lazy val questDbClient: QuestDbClient = QuestDbClient("http://localhost:9000", backend)

  protected lazy val table: String = s"${this.getClass.getName}_trades".replace('.', '_').toLowerCase()

  override def beforeAll(): Unit = {
    val sqlQuery =
      s"CREATE TABLE IF NOT EXISTS $table(date TIMESTAMP, instrument SYMBOL, quantity Long, price Double, side String) timestamp(date) PARTITION BY DAY"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))

    val sqlQueryML = s"INSERT INTO $table VALUES('2021-10-05T11:31:35.878Z', 'AAPL', 245, 123.4, 'C'), " +
      s"('2021-10-05T12:31:35.878Z', 'AAPL', 245, 123.3, 'C'), " +
      s"('2021-10-05T13:31:35.878Z', 'AAPL', 250, 123.1, 'C'), " +
      s"('2021-10-05T15:31:35.878Z', 'AAPL', 265, 128.1, 'A'), " +
      s"('2021-10-05T13:32:35.878Z', 'AAPL', 250, 123.1, 'B'), " +
      s"('2021-10-05T14:31:35.878Z', 'AAPL', 250, 123.0, 'C');"

    val executionResultML = questDbClient.executeSql(sqlQueryML, 60.seconds)
    assertEquals(executionResultML.get("ddl"), Some("OK"))
    waitForQuestDbExecution()
  }

  override def afterAll(): Unit = {
    val sqlQuery        = s"DROP TABLE '$table';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))
    waitForQuestDbExecution()
  }
}
