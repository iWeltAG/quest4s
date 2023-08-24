package de.iwelt.quest4s.pekkohttp

import de.iwelt.quest4s.{AbstractExportSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class PekkoExportCSVSuite extends AbstractExportSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.pekkoHttpBackend
}
