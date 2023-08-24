package de.iwelt.quest4s.pekkohttp

import de.iwelt.quest4s.{AbstractQuerySuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class PekkkoQuerySuite extends AbstractQuerySuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.pekkoHttpBackend
}
