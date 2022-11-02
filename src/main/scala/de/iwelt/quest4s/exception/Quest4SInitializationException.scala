package de.iwelt.quest4s.exception

class Quest4SInitializationException(fieldName: String, clazz: Class[_]) extends Exception(s"$fieldName is not inizialized in ${clazz.getSimpleName}")
