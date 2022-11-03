package de.iwelt.quest4s.okhttp

import de.iwelt.quest4s.{AbstractQuerySuite, TestAdditions}
import sttp.client3.SttpBackend

import scala.concurrent.Future

class OkHttpQuerySuite extends AbstractQuerySuite {
  override val backend: SttpBackend[Future, _] = TestAdditions.okHttpBackend
}
