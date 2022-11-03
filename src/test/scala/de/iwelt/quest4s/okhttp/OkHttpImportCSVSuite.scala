package de.iwelt.quest4s.okhttp

import de.iwelt.quest4s.{AbstractImportSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class OkHttpImportCSVSuite extends AbstractImportSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.okHttpBackend
}
