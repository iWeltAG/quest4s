package de.iwelt.quest4s.akkahttp

import de.iwelt.quest4s.{AbstractExportSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class AkkaExportCSVSuite extends AbstractExportSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.akkaHttpBackend
}
