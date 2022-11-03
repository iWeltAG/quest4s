package de.iwelt.quest4s.okhttp

import de.iwelt.quest4s.{AbstractExportSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class OkHttpExportCSVSuite extends AbstractExportSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.okHttpBackend
}
