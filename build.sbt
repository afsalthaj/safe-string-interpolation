import microsites.CdnDirectives

lazy val root = (project in file("."))
  .dependsOn(macros)
  .settings(
    name := "safe-string",
    rootBuildSettings,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
)

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
      "org.scala-lang" % "scala-reflect" % "2.12.6",
      "org.specs2" %% "specs2-scalaz" % "4.2.0"
    ),
    rootBuildSettings,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )

lazy val test = (project in file("test"))
  .settings(
    name := "test",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-scalacheck" % "4.2.0" % "test",
      "org.specs2" %% "specs2-scalaz" % "4.2.0" % "test"
    ),
    rootBuildSettings,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  ).dependsOn(macros)

enablePlugins(MicrositesPlugin)

lazy val rootBuildSettings = Seq(
  pgpSecretRing := file("./travis/local.secring.asc"),
  pgpPublicRing := file("./travis/local.pubring.asc"),
  pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray),
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sys.env.getOrElse("SONATYPE_USER", ""),
    sys.env.getOrElse("SONATYPE_PASS", "")
  ),
  organization := "io.github.afsalthaj",
  organizationName := "safe-string-interpolation",
  organizationHomepage := Some(url("https://afsalthaj.github.io/safe-string-interpolation/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/afsalthaj/safe-string-interpolation"),
      "scm:git@github.com:afsalthaj/safe-string-interpolation.git"
    )),
  developers := List(
    Developer(
      id = "afsalthaj",
      name = "Afsal Thaj",
      email = "afsal.taj06@gmail.com",
      url = url("https://medium.com/@afsal.taj06")
    )
  ),

  description := "Typesafe string interpolation and typedriven secret logging",
  licenses := List("Apache 2" -> new URL("https://raw.githubusercontent.com/afsalthaj/safe-string-interpolation/master/LICENSE")),
  homepage := Some(url("https://github.com/afsalthaj/safe-string-interpolation")),

  // Remove all additional repository other than Maven Central from POM
  pomIncludeRepository := { _ => false },
  publishMavenStyle := true
)
