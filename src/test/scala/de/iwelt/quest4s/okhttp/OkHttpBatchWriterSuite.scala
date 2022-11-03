package de.iwelt.quest4s.okhttp

import akka.actor.ActorSystem
import de.iwelt.quest4s.{AbstractBatchWriterSuite, QuestDbBatchWriter, TestAdditions}
import org.joda.time.DateTime
import sttp.client3.SttpBackend

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

class OkHttpBatchWriterSuite extends AbstractBatchWriterSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.okHttpBackend

  override def beforeAll(): Unit = {
    super.beforeAll()
    QuestDbBatchWriter.questDbClient = questDbClient
    QuestDbBatchWriter.batchSize = 50
    QuestDbBatchWriter.writeInterval = 5.seconds
    QuestDbBatchWriter.actorSystemOption = Some(ActorSystem())
  }

  override def beforeEach(context: BeforeEach): Unit = {
    super.beforeEach(context)
    QuestDbBatchWriter.flush(true)
  }

  test("Insert Data by using Global Batch Writer") {
    val countRowsStartTable       = countRows(table)
    val countRowsStartSecondTable = countRows(secondTable)
    (1 to 30).foreach(index => {
      var baseMap = Map("date" -> new DateTime(), "message" -> s"message with index $index")
      if (index % 2 == 0) {
        baseMap ++= Map("target" -> getClass.getSimpleName)
      }
      QuestDbBatchWriter.addRecord(secondTable, baseMap)
    })

    assertEquals(QuestDbBatchWriter.countCurrentRecords, 30L)
    assertEquals(countRows(secondTable), countRowsStartSecondTable)
    assertEquals(countRows(table), countRowsStartTable)

    (1 to 5).foreach(_ => {
      val baseMap = Map(
        "date"       -> new DateTime(),
        "instrument" -> "AAPL",
        "quantity"   -> Random.nextInt(),
        "price"      -> Random.nextDouble(),
        "side"       -> Random.alphanumeric.take(1).mkString
      )
      QuestDbBatchWriter.addRecord(table, baseMap)
    })

    assertEquals(QuestDbBatchWriter.countCurrentRecords, 35L)
    assertEquals(countRows(secondTable), countRowsStartSecondTable)
    assertEquals(countRows(table), countRowsStartTable)
    Thread.sleep(6.seconds.toMillis)
    assertEquals(QuestDbBatchWriter.countCurrentRecords, 0L)
    assertEquals(countRows(secondTable), countRowsStartSecondTable + 30)
    assertEquals(countRows(table), countRowsStartTable + 5)
  }
}
