package de.iwelt.quest4s.akkahttp

import de.iwelt.quest4s.{AbstractExtendedSelectSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class AkkaExtendedSelectSuite extends AbstractExtendedSelectSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.akkaHttpBackend
}
