package de.iwelt.quest4s

import de.iwelt.quest4s.converter.QueryResponseConverter
import org.joda.time.DateTime

import scala.concurrent.duration.DurationInt
import scala.util.Random

abstract class AbstractBatchWriterSuite extends BaseSuite {

  protected lazy val secondTable = s"${table}_2"

  lazy val batchWriter: QuestDbBatchWriter = QuestDbBatchWriter(questDbClient, 50, 60.seconds)

  override def beforeAll(): Unit = {
    super.beforeAll()
    val sqlQuery        = s"CREATE TABLE IF NOT EXISTS $secondTable(date TIMESTAMP, message String, target String) timestamp(date) PARTITION BY DAY"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))
    waitForQuestDbExecution()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    val sqlQuery        = s"DROP TABLE '$secondTable';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("ddl"), Some("OK"))
    waitForQuestDbExecution()
  }

  override def beforeEach(context: BeforeEach): Unit = {
    batchWriter.flush(true)
    waitForQuestDbExecution()
  }

  test("Insert Data by individual Global Batch Writer") {
    val countRowsStartTable       = countRows(table)
    val countRowsStartSecondTable = countRows(secondTable)
    (1 to 30).foreach(index => {
      var baseMap = Map("date" -> new DateTime(), "message" -> s"message with index $index")
      if (index % 2 == 0) {
        baseMap ++= Map("target" -> getClass.getSimpleName)
      }
      batchWriter.addRecord(secondTable, baseMap)
    })

    Thread.sleep(1.seconds.toMillis)
    assertEquals(batchWriter.countCurrentRecords, 30L)
    assertEquals(countRows(secondTable), countRowsStartSecondTable)
    assertEquals(countRows(table), countRowsStartTable)

    (1 to 30).foreach(_ => {
      val baseMap = Map(
        "date"       -> new DateTime(),
        "instrument" -> "AAPL",
        "quantity"   -> Random.nextInt(),
        "price"      -> Random.nextDouble(),
        "side"       -> Random.alphanumeric.take(1).mkString
      )
      batchWriter.addRecord(table, baseMap)
    })
    Thread.sleep(1.seconds.toMillis)
    assertEquals(batchWriter.countCurrentRecords, 10L)
    assertEquals(countRows(secondTable), countRowsStartSecondTable + 30)
    assertEquals(countRows(table), countRowsStartTable + 20)
  }

  def countRows(tableName: String): Long = {
    QueryResponseConverter.convertMapResponseToSelectResponse(questDbClient.executeSql(s"select * from $tableName", 60.seconds)).count.getOrElse(0)
  }
}
