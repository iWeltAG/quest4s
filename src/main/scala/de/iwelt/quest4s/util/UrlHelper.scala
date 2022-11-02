package de.iwelt.quest4s.util

import io.mikael.urlbuilder.UrlBuilder

import java.util.Date
import scala.jdk.CollectionConverters._

trait UrlHelper {

  def buildRequestUrl(url: String, queryParameter: Map[String, Any]): String = {
    var urlBuilder = UrlBuilder.fromString(url)
    queryParameter.foreach { param =>
      param._2 match {
        case option: Option[_] =>
          option.foreach(element => urlBuilder = urlBuilder.addParameter(param._1, convertValue(element)))
        case list: Iterable[_] =>
          list.foreach(element => urlBuilder = urlBuilder.addParameter(param._1, convertValue(element)))
        case list: java.lang.Iterable[_] =>
          list.asScala.foreach(element => urlBuilder = urlBuilder.addParameter(param._1, convertValue(element)))
        case _ =>
          urlBuilder = urlBuilder.addParameter(param._1, convertValue(param._2))
      }
    }
    urlBuilder.toString
  }

  private def convertValue(value: Any): String = {
    value match {
      case date: Date =>
        date.toInstant.toString
      case date: org.joda.time.DateTime =>
        convertValue(date.toDate)
      case any: Any =>
        any.toString
    }
  }

}
