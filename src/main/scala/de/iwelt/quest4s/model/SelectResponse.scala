package de.iwelt.quest4s.model

case class SelectResponse(query: String, data: List[Map[String, Any]], count: Option[Long], timings: Option[Timing], explain: Option[Explain])
