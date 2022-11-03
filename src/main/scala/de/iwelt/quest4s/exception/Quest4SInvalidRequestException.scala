package de.iwelt.quest4s.exception
import sttp.client3.ResponseException

class Quest4SInvalidRequestException(responseException: ResponseException[_, _]) extends Exception(s"${responseException.getMessage}")
