package de.iwelt.quest4s

import scala.concurrent.duration.DurationInt

abstract class AbstractExportSuite extends BaseSuite {

  test(s"Export File to CSV") {
    val sqlQuery = s"Select * from $table WHERE instrument = 'AAPL' and side = 'C';"
    val file     = questDbClient.exportCsv(sqlQuery, 60.seconds)
    assertEquals(file.lineCount, 5L)
    assertEquals(file.contentAsString.contains("price"), true)
    file
  }

}
