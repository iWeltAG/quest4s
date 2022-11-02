jsonFiles += (baseDirectory.value / "package.json")

organization := jsonHandler.value.stringValue("package.json", "organization")

name := jsonHandler.value.stringValue("package.json", "name")

homepage := Some(url("https://github.com/iWeltAG/quest4s"))

scmInfo := Some(ScmInfo(url("https://github.com/iWeltAG/quest4s"), "https://github.com/iWeltAG/quest4s.git"))

developers := List(
  Developer("QuadStingray", "QuadStingray", "github@quadstingray.dev", url("https://github.com/QuadStingray")),
  Developer("sfxcode", "sfxcode", "tom@sfxcode.com", url("https://github.com/sfxcode"))
)

licenses += ("MIT License", url("https://https://github.com/iWeltAG/quest4s/blob/main/LICENSE"))

description := "REST client to connect with QuestDB based on Scala."

publishMavenStyle := true
