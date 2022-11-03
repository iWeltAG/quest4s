package de.iwelt.quest4s

import de.iwelt.quest4s.converter.QueryResponseConverter
import de.iwelt.quest4s.model.Explain
import org.joda.time.DateTime

import scala.concurrent.duration.DurationInt

abstract class AbstractExtendedSelectSuite extends BaseSuite {

  test(s"Simple filtered Select Data and convert") {
    val sqlQuery        = s"Select * from $table WHERE instrument = 'AAPL' and side = 'C';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("query"), Some(sqlQuery))
    assertEquals(executionResult.contains("columns"), true)
    assertEquals(executionResult.contains("dataset"), true)
    assertEquals(executionResult.get("count"), Some(4))
    val selectResponse = QueryResponseConverter.convertMapResponseToSelectResponse(executionResult)
    assertEquals(selectResponse.query, sqlQuery)
    assertEquals(selectResponse.count, Some(4L))
    assertEquals(selectResponse.data.size, 4)

    val headOfData = selectResponse.data.head
    assertEquals(headOfData.get("date"), Some(new DateTime("2021-10-05T11:31:35.878000Z")))
    assertEquals(headOfData.get("instrument"), Some("AAPL"))
    assertEquals(headOfData.get("quantity"), Some(245))
    assertEquals(headOfData.get("price"), Some(123.4))
    assertEquals(headOfData.get("side"), Some("C"))

    assertEquals(selectResponse.explain, None)
    assertEquals(selectResponse.timings, None)
  }

  test(s"Group By Select Data and convert") {

    val sqlQuery        = s"Select side, SUM(price) as priceSum from $table WHERE instrument = 'AAPL' and side = 'C' GROUP BY side;"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds)
    assertEquals(executionResult.get("query"), Some(sqlQuery))
    assertEquals(executionResult.contains("columns"), true)
    assertEquals(executionResult.contains("dataset"), true)
    assertEquals(executionResult.get("count"), Some(1))
    val selectResponse = QueryResponseConverter.convertMapResponseToSelectResponse(executionResult)
    assertEquals(selectResponse.query, sqlQuery)
    assertEquals(selectResponse.count, Some(1L))
    assertEquals(selectResponse.data.size, 1)

    val headOfData = selectResponse.data.head
    assertEquals(headOfData.get("date"), None)
    assertEquals(headOfData.get("priceSum"), Some(492.799999999999))
    assertEquals(headOfData.get("side"), Some("C"))
    assertEquals(headOfData.get("instrument"), None)

    assertEquals(selectResponse.explain, None)
    assertEquals(selectResponse.timings, None)
  }

  test(s"Simple Select Data with timing and explain and convert") {

    val sqlQuery        = s"Select * from $table WHERE instrument = 'AAPL' and side = 'C';"
    val executionResult = questDbClient.executeSql(sqlQuery, 60.seconds, Map("timings" -> true, "explain" -> true))
    assertEquals(executionResult.get("query"), Some(sqlQuery))
    assertEquals(executionResult.contains("columns"), true)
    assertEquals(executionResult.contains("dataset"), true)
    assertEquals(executionResult.get("count"), Some(4))
    val selectResponse = QueryResponseConverter.convertMapResponseToSelectResponse(executionResult)
    assertEquals(selectResponse.query, sqlQuery)
    assertEquals(selectResponse.count, Some(4L))
    assertEquals(selectResponse.data.size, 4)

    val headOfData = selectResponse.data.head
    assertEquals(headOfData.get("date"), Some(new DateTime("2021-10-05T11:31:35.878000Z")))
    assertEquals(headOfData.get("instrument"), Some("AAPL"))
    assertEquals(headOfData.get("quantity"), Some(245))
    assertEquals(headOfData.get("price"), Some(123.4))
    assertEquals(headOfData.get("side"), Some("C"))

    assertEquals(selectResponse.explain, Some(Explain(false)))
    assertEquals(selectResponse.timings.isDefined, true)
  }

}
