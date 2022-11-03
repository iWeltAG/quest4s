package de.iwelt.quest4s.akkahttp

import de.iwelt.quest4s.{AbstractBatchWriterSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class AkkaBatchWriterSuite extends AbstractBatchWriterSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.akkaHttpBackend
}
