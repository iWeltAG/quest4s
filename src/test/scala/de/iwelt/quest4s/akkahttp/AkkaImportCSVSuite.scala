package de.iwelt.quest4s.akkahttp

import de.iwelt.quest4s.{AbstractImportSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class AkkaImportCSVSuite extends AbstractImportSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.akkaHttpBackend
}
