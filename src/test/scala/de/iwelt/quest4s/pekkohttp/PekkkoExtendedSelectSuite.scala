package de.iwelt.quest4s.pekkohttp

import de.iwelt.quest4s.{AbstractExtendedSelectSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class PekkkoExtendedSelectSuite extends AbstractExtendedSelectSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.pekkoHttpBackend
}
