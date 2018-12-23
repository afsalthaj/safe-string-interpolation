lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.scala-lang"  %  "scala-reflect" %  "2.12.6",
      "org.specs2"     %% "specs2-scalaz"  %  "4.2.0"
    )
  )

lazy val test = (project in file("test"))
  .settings(
    name := "test",
    libraryDependencies ++= Seq(
      "org.specs2"     %% "specs2-scalacheck"  %  "4.2.0" % "test",
      "org.specs2"     %% "specs2-scalaz"  %  "4.2.0" % "test"
    )
  ).dependsOn(core)