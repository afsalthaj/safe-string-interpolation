import microsites.CdnDirectives

lazy val root = (project in file("."))
  .dependsOn(macros)
  .settings(
    name := "safe-string-interpolation"
  ).aggregate(macros, test)

lazy val docs = project
  .enablePlugins(MicrositesPlugin)
  .settings(name := "afsalthaj")
  .settings(moduleName := "safe-string-interpolation-docs")
  .settings(DocSupport.settings)
  .settings(Seq(
    fork in tut := true,
    git.remoteRepo := "https://github.com/afsalthaj/safe-string-interpolation.git",
    includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md"
  ))
  .settings(scalacOptions in Tut ~= (_.filterNot(Set("-Ywarn-unused-import", "-Ywarn-dead-code"))))
  .enablePlugins(GhpagesPlugin)


micrositeCDNDirectives := CdnDirectives(
  jsList = List(
    "https://cdnjs.cloudflare.com/ajax/libs/ag-grid/7.0.2/ag-grid.min.js",
    "https://cdnjs.cloudflare.com/ajax/libs/ajaxify/6.6.0/ajaxify.min.js"
  ),
  cssList = List(
    "https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.5.2/animate.css",
    "https://cdnjs.cloudflare.com/ajax/libs/cssgram/0.1.12/1977.min.css",
    "https://cdnjs.cloudflare.com/ajax/libs/cssgram/0.1.12/brooklyn.css"
  )
)

micrositeGithubOwner := "afsalthaj"

lazy val macros = (project in file("macros"))
  .settings(
    name := "safe-string-macros",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.12.14",
      "org.specs2" %% "specs2-scalaz" % "4.8.2"
    ),  
  )

lazy val test = (project in file("test"))
  .settings(
    name := "test",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-scalacheck" % "4.8.2" % "test",
      "org.specs2" %% "specs2-scalaz" % "4.8.2" % "test"
    ),
  ).dependsOn(macros)

enablePlugins(MicrositesPlugin)

inThisBuild(List(
  organization := "io.github.afsalthaj",
  homepage := Some(url("https://afsalthaj.github.io/safe-string-interpolation/")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "afsalthaj",
      "Afsal Thaj",
      "afsal.taj06@gmail.com",
      url("https://medium.com/@afsal.taj06")
    )
  )
))

