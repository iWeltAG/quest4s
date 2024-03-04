package de.iwelt.quest4s
import sttp.capabilities
import sttp.capabilities.akka.AkkaStreams
import sttp.capabilities.pekko.PekkoStreams
import sttp.client3.SttpBackend
import sttp.client3.akkahttp.AkkaHttpBackend
import sttp.client3.okhttp.OkHttpFutureBackend
import sttp.client3.pekkohttp.PekkoHttpBackend

import scala.concurrent.Future

object TestAdditions {
  lazy val pekkoHttpBackend: SttpBackend[Future, PekkoStreams with capabilities.WebSockets] = PekkoHttpBackend()
  lazy val okHttpBackend: SttpBackend[Future, capabilities.WebSockets]                      = OkHttpFutureBackend()
}
