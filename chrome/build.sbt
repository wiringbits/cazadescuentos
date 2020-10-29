import com.alexitc.ChromeSbtPlugin

lazy val commonJsLib = ProjectRef(file("../lib"), "commonJS")
lazy val apiJsLib = ProjectRef(file("../lib"), "apiJS")

lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"
lazy val appName = "cazadescuentos"

val circe = "0.13.0"
val sttp = "2.1.2"

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      name := appName,
      version := "1.27",
      scalaVersion := "2.13.3",
      scalacOptions ++= Seq(
        "-language:implicitConversions",
        "-language:existentials",
        "-Xlint",
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      ),
      scalacOptions += "-Ymacro-annotations",
      requireJsDomEnv in Test := true
    )

lazy val bundlerSettings: Project => Project = {
  _.settings(
    // NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
    // webpack settings.
    scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
    version in webpack := "4.8.1",
    webpackConfigFile := {
      val file = if (isProductionBuild) "production.webpack.config.js" else "dev.webpack.config.js"
      Some(baseDirectory.value / file)
    },
    // scala-js-chrome
    scalaJSLinkerConfig := scalaJSLinkerConfig.value.withRelativizeSourceMapBase(
      Some((Compile / fastOptJS / artifactPath).value.toURI)
    ),
    skip in packageJSDependencies := false,
    webpackBundlingMode := BundlingMode.Application,
    fastOptJsLib := (webpack in (Compile, fastOptJS)).value.head,
    fullOptJsLib := (webpack in (Compile, fullOptJS)).value.head,
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    // you can customize and have a static output name for lib and dependencies
    // instead of having the default files names like extension-fastopt.js, ...
    artifactPath in (Compile, fastOptJS) := {
      (crossTarget in (Compile, fastOptJS)).value / "main.js"
    },
    artifactPath in (Compile, fullOptJS) := {
      (crossTarget in (Compile, fullOptJS)).value / "main.js"
    }
  )
}

lazy val buildInfoSettings: Project => Project = {
  _.enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoPackage := "net.cazadescuentos",
      buildInfoKeys := Seq[BuildInfoKey](name),
      buildInfoKeys ++= Seq[BuildInfoKey](
        "production" -> isProductionBuild
      ),
      buildInfoUsePackageAsPath := true
    )
}

lazy val root = (project in file("."))
  .dependsOn(commonJsLib, apiJsLib)
  .enablePlugins(ChromeSbtPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)
  .configure(baseSettings, bundlerSettings, buildInfoSettings)
  .settings(
    chromeManifest := AppManifest.generate(appName, Keys.version.value),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "@types/classnames" -> "2.2.10",
      "react-router-dom" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circe,
      "io.circe" %%% "circe-generic" % circe,
      "io.circe" %%% "circe-parser" % circe,
      "com.softwaremill.sttp.client" %%% "core" % sttp
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.1.1" % Test
    ),
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "1.0.0",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    libraryDependencies += "com.alexitc" %%% "scala-js-chrome" % "0.7.0",
    libraryDependencies += "com.softwaremill.sttp.client" %%% "core" % sttp,
    libraryDependencies += "org.lrng.binding" %%% "html" % "1.0.3"
  )
