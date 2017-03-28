
organization := "net.xrrocha"
name := "scala-memory-image"
version := "0.1.0"

scalaVersion in ThisBuild := "2.12.1" // "2.11.8"

scalacOptions ++= Seq(
  "-Xplugin-require:macroparadise",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:existentials"
)

// temporary workaround for https://github.com/scalameta/paradise/issues/10
scalacOptions in(Compile, console) := Seq() // macroparadise plugin doesn't work in repl yet.
scalacOptions in(Test, console) := Seq() // macroparadise plugin doesn't work in repl yet.
// temporary workaround for https://github.com/scalameta/paradise/issues/55
sources in(Compile, doc) := Nil // macroparadise doesn't work with scaladoc yet.
sources in(Test, doc) := Nil // macroparadise doesn't work with scaladoc yet.

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayIvyRepo("scalameta", "maven")
)

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "1.6.0",
  "com.lihaoyi" %% "upickle" % "0.4.4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.lihaoyi" %% "utest" % "0.4.5" % "test",
  "com.lihaoyi" % "ammonite" % "0.8.2" % "test" cross CrossVersion.full
)

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M7" cross CrossVersion.full)

testFrameworks += new TestFramework("utest.runner.Framework")

fork in run := true

autoScalaLibrary := true
initialCommands in (Test, console) := """ammonite.Main().run()"""

initialize := {
  System.setProperty("jline.terminal", "unix")
}
