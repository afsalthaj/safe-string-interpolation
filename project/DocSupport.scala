import com.typesafe.sbt.GitPlugin.autoImport.git
import com.typesafe.sbt.sbtghpages.GhpagesPlugin.autoImport.ghpagesNoJekyll
import microsites.MicrositesPlugin.autoImport._
import sbt.Keys.{autoAPIMappings, fork, includeFilter, version}
import sbt.{File, IO, taskKey}

object DocSupport {
  lazy val updateDoc = taskKey[Unit]("update versions in doc")

  val settings = Seq(

  updateDoc := {
    val string = IO.read(new File("docs/src/main/tut/index.md").getAbsoluteFile)
    val version1 =
      "\"io.github.afsalthaj\" %% \"safe-string\".*".r.findFirstIn(string).getOrElse(throw new IllegalStateException("Cannot find pattern  \"\\\"io.github.afsalthaj\\\" %% \\\"safe-string\\\".*\" in docs/src/main/tut/index.md"))
    val version2 =
      "`io.github.afsalthaj::safe-string.*".r.findFirstIn(string).getOrElse(throw new IllegalStateException("Cannot find the pattern io.github.afsalthaj::safe-string.* in docs/src/main/tut/index.md"))

    val content =
      string.replace(version1, s""""io.github.afsalthaj" %% "safe-string" % "${version.value}" """).replace(version2, s"`io.github.afsalthaj::safe-string:${version.value}`")

    IO.write(new File("docs/src/main/tut/index.md").getAbsoluteFile, content = content, append = false)

    publishMicrosite.value
  },
    micrositeName := "Typesafe Interpolation",
    micrositeDescription := "Typesafe Interpolation",
    micrositeHighlightTheme := "atom-one-light",
    micrositeGithubRepo := "safe-string-interpolation",
    micrositeHomepage := "https://afsalthaj.github.io/safe-string-interpolation",
    micrositeBaseUrl := "iagcl/safe-string-interpolation",
    micrositeGithubOwner := "afsalthaj",
    micrositeGithubRepo := "safe-string-interpolation",
    micrositeGitterChannelUrl := "safe-string-interpolation/community",
    micrositePushSiteWith := GHPagesPlugin,
    micrositePalette := Map(
      "brand-primary" -> "#5B5988",
      "brand-secondary" -> "#292E53",
      "brand-tertiary" -> "#222749",
      "gray-dark" -> "#49494B",
      "gray" -> "#7B7B7E",
      "gray-light" -> "#E5E5E6",
      "gray-lighter" -> "#F4F3F4",
      "white-color" -> "#FFFFFF"),
    autoAPIMappings := true,
    ghpagesNoJekyll := false,
  )
}