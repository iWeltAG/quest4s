package de.iwelt.quest4s

import better.files.File
import de.iwelt.quest4s.converter.QueryResponseConverter

import scala.concurrent.duration.DurationInt

abstract class AbstractImportSuite extends BaseSuite {

  override def beforeEach(context: BeforeEach): Unit = {
    super.afterAll()
    super.beforeAll()
  }

  test(s"Upload an file") {
    val countBeforeUpload = countRows
    val file              = File("src/test/resources/import-test.csv")
    questDbClient.importCsv(table, file, 300.seconds)
    waitForQuestDbExecution()
    val countAfterUpload = countRows
    assertEquals(countBeforeUpload + 4, countAfterUpload)
  }

  def countRows: Long = {
    val count = QueryResponseConverter.convertMapResponseToSelectResponse(questDbClient.executeSql(s"select * from $table", 60.seconds)).count.getOrElse(-1L)
    assertNotEquals(count, -1L)
    count
  }

}
