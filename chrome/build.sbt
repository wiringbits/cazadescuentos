import com.alexitc.ChromeSbtPlugin

lazy val commonJsLib = ProjectRef(file("../lib"), "commonJS")
lazy val apiJsLib = ProjectRef(file("../lib"), "apiJS")
lazy val uiJsLib = ProjectRef(file("../lib"), "ui")

lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"
lazy val appName = "cazadescuentos"

val circe = "0.14.1"
val sttp = "3.5.2"

val scalaDomVersion = "2.1.0"
val scalaTestVersion = "3.2.10"
val scalaJsChromeVersion = "0.9.0"

val slinkyVersion = "0.7.2"
val muiFacadeVersion = "0.2.0"
val macroTaskExecutorVersion = "1.0.0"
val javaTimeVersion = "2.4.0"

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      name := appName,
      version := "1.28",
      scalaVersion := "2.13.7",
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
    useYarn := true,
    // NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
    // webpack settings.
    scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
    version in webpack := "4.8.1",
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

lazy val withCssLoading: Project => Project = {
  _.settings(
    /* custom webpack file to include css */
    webpackConfigFile := {
      val file = "custom.webpack.config.js"
      Some(baseDirectory.value / file)
    },
    // running `sbt test` fails if the webpack config is specified, it seems to happen because
    // the default webpack config from scalajs-bundler isn't written, making `sbt test` depend on
    // the chromeUnpackedFast task ensures that such config is generated, there might be a better
    // solution but this works for now.
    Test / test := (Test / test).dependsOn(chromeUnpackedFast).value,
    webpackConfigFile in Test := Some(baseDirectory.value / "test.webpack.config.js"),
    Compile / npmDevDependencies ++= Seq(
      "webpack-merge" -> "4.2.2",
      "css-loader" -> "3.4.2",
      "style-loader" -> "1.1.3",
      "file-loader" -> "5.1.0",
      "url-loader" -> "3.0.0"
    )
  )
}

// specify versions for all of reacts dependencies
lazy val reactNpmDeps: Project => Project = {
  _.settings(
    stTypescriptVersion := "3.9.3",
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "csstype" -> "2.6.11",
      "react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
    ),
    Compile / npmDevDependencies ++= Seq(
      "@types/react" -> "16.9.42",
      "@types/react-dom" -> "16.9.8",
      "@types/prop-types" -> "15.7.3",
      "@types/react-router" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2"
    )
  )
}

lazy val root = (project in file("."))
  .dependsOn(commonJsLib, apiJsLib, uiJsLib)
  .enablePlugins(ChromeSbtPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)
  .configure(baseSettings, bundlerSettings, buildInfoSettings, reactNpmDeps, withCssLoading)
  .settings(
    chromeManifest := AppManifest.generate(appName, Keys.version.value),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stTypescriptVersion := "3.9.3",
    stUseScalaJsDom := true,
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    Compile / stMinimize := Selection.All,
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "@types/classnames" -> "2.2.10"
    ),
    libraryDependencies ++= Seq(
      "com.alexitc" %%% "scala-js-chrome" % scalaJsChromeVersion,
      // scala dom
      "org.scala-js" %%% "scalajs-dom" % scalaDomVersion,
      // scala test
      "org.scalatest" %%% "scalatest" % scalaTestVersion % Test,
      // scala-js-macrotask-executor
      "org.scala-js" %%% "scala-js-macrotask-executor" % macroTaskExecutorVersion,
      // circe
      "io.circe" %%% "circe-core" % circe,
      "io.circe" %%% "circe-generic" % circe,
      "io.circe" %%% "circe-parser" % circe,
      // sttp
      "com.softwaremill.sttp.client3" %%% "core" % sttp,
      // scala-java-time
      "io.github.cquiroz" %%% "scala-java-time" % javaTimeVersion,
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % javaTimeVersion,
      // material
      "com.alexitc" %%% "sjs-material-ui-facade" % muiFacadeVersion // material-ui bindings
    )
  )
