package de.iwelt.quest4s

import akka.actor.ActorSystem
import better.files.File
import com.github.tototoshi.csv.CSVWriter
import de.iwelt.quest4s.exception.Quest4SInitializationException
import org.joda.time.DateTime

import java.util.Date
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.{ DurationInt, FiniteDuration }

case class QuestDbBatchWriter(questDbClient: QuestDbClient, batchSize: Int, writeInterval: FiniteDuration, actorSystemOption: Option[ActorSystem] = None) {

  private val collectionBuffer: mutable.Map[String, ArrayBuffer[Map[String, Any]]] = mutable.Map()

  actorSystemOption.foreach(actorSystem => {
    import actorSystem.dispatcher
    actorSystem.scheduler.scheduleWithFixedDelay(writeInterval, writeInterval) { () =>
      flush(force = true)
    }
  })

  sys.addShutdownHook { () =>
    flush(force = true)
  }

  def flush(force: Boolean = false): Unit = {
    if (countCurrentRecords >= batchSize || force) {
      collectionBuffer.map { element =>
        val recordBuffer = element._2
        val tableName    = element._1
        collectionBuffer.remove(tableName)
        val f      = File.temporaryFile(suffix = ".csv").get()
        val writer = CSVWriter.open(f.toJava)
        val keys   = recordBuffer.flatMap(_.keySet).distinct.sorted.toList
        writer.writeRow(keys)
        recordBuffer.foreach(record => {
          val list: List[Any] = keys.map(key => {
            val value: Any = if (record.get(key) != null && record.contains(key)) {
              record(key) match {
                case dateTime: DateTime =>
                  dateTime.toInstant.toString()
                case date: Date =>
                  new DateTime(date).toInstant.toString()
                case any: Any => any
              }
            }
            else {
              ""
            }
            value
          })
          writer.writeRow(list)
        })
        writer.flush()
        writer.close()
        questDbClient.importCsv(tableName, f, 60.seconds)
      }
    }
  }

  def addRecord(table: String, data: Map[String, Any]): Unit = {
    val buffer: ArrayBuffer[Map[String, Any]] = collectionBuffer.getOrElse(
      table, {
        val newArrayBuffer = ArrayBuffer[Map[String, Any]]()
        collectionBuffer.put(table, newArrayBuffer)
        newArrayBuffer
      }
    )
    buffer.+=(data)
    flush()
  }

  def countCurrentRecords: Long = {
    var count = 0
    collectionBuffer.foreach(element => count += element._2.size)
    count
  }

}

object QuestDbBatchWriter {

  var questDbClient: QuestDbClient           = _
  var batchSize: Int                         = _
  var writeInterval: FiniteDuration          = _
  var actorSystemOption: Option[ActorSystem] = _

  private lazy val batchWriter = {
    if (questDbClient == null) {
      throw new Quest4SInitializationException("questDbClient", this.getClass)
    }
    if (batchSize == null) {
      throw new Quest4SInitializationException("batchSize", this.getClass)
    }
    if (writeInterval == null) {
      throw new Quest4SInitializationException("writeInterval", this.getClass)
    }
    if (actorSystemOption == null) {
      throw new Quest4SInitializationException("actorSystemOption", this.getClass)
    }
    QuestDbBatchWriter(questDbClient, batchSize, writeInterval, actorSystemOption)
  }

  def countCurrentRecords: Long                              = batchWriter.countCurrentRecords
  def addRecord(table: String, data: Map[String, Any]): Unit = batchWriter.addRecord(table, data)
  def flush(force: Boolean = false): Unit                    = batchWriter.flush(force)
}
