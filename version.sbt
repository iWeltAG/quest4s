ThisBuild / version := jsonHandler.value.stringValue("package.json", "version").trim.toLowerCase.replace(".snapshot", "-SNAPSHOT").trim
