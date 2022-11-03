package de.iwelt.quest4s
import sttp.capabilities
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.SttpBackend
import sttp.client3.akkahttp.AkkaHttpBackend
import sttp.client3.okhttp.OkHttpFutureBackend

import scala.concurrent.Future

object TestAdditions {
  val akkaHttpBackend: SttpBackend[Future, AkkaStreams with capabilities.WebSockets] = AkkaHttpBackend()
  val okHttpBackend: SttpBackend[Future, capabilities.WebSockets]   = OkHttpFutureBackend()
}
