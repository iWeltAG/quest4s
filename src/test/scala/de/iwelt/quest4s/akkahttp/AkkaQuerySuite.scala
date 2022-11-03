package de.iwelt.quest4s.akkahttp

import de.iwelt.quest4s.{AbstractQuerySuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class AkkaQuerySuite extends AbstractQuerySuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.akkaHttpBackend
}
