package de.iwelt.quest4s.converter
import de.iwelt.quest4s.model.{Explain, SelectResponse, Timing}
import org.joda.time.DateTime

object QueryResponseConverter {

  def convertMapResponseToSelectResponse(mapResponse: Map[String, Any]): SelectResponse = {

    val maybeTiming: Option[Timing] = mapResponse
      .get("timings")
      .map(value => {
        val map = value.asInstanceOf[Map[String, Any]]
        Timing(map("compiler").toString.toLong, map("count").toString.toLong, map("execute").toString.toLong)
      })

    val maybeExplain: Option[Explain] = mapResponse
      .get("explain")
      .map(value => {
        val map = value.asInstanceOf[Map[String, Any]]
        Explain(map("jitCompiled").toString.toBoolean)
      })

    val keys = mapResponse("columns").asInstanceOf[List[Map[String, String]]]

    val dataList: List[Map[String, Any]] = mapResponse("dataset")
      .asInstanceOf[List[List[Any]]]
      .map(data => {
        data.zipWithIndex
          .map(element => {
            val keyElement = keys(element._2)
            val key        = keyElement("name")
            val dataType   = keyElement("type")
            if ("timestamp".equalsIgnoreCase(dataType) || "date".equalsIgnoreCase(dataType)) {
              (key, new DateTime(element._1))
            }
            else {
              (key, element._1)
            }
          })
          .toMap
      })

    SelectResponse(
      mapResponse("query").toString,
      dataList,
      mapResponse.get("count").map(_.toString.toLong),
      maybeTiming,
      maybeExplain
    )
  }

}
