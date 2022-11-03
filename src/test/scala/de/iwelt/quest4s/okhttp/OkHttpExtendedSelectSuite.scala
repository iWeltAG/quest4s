package de.iwelt.quest4s.okhttp

import de.iwelt.quest4s.{AbstractExtendedSelectSuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class OkHttpExtendedSelectSuite extends AbstractExtendedSelectSuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.okHttpBackend
}
