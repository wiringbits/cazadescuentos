ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "net.wiringbits"

val circe = "0.14.1"
val sttp = "3.6.2"

val scalaDomVersion = "2.1.0"
val scalaTestVersion = "3.2.10"
val scalaJsChromeVersion = "0.9.0"

val slinkyVersion = "0.7.2"
val muiFacadeVersion = "0.2.0"
val macroTaskExecutorVersion = "1.0.0"
val javaTimeVersion = "2.3.0"

lazy val commonJsLib = ProjectRef(file("../lib"), "commonJS")
lazy val apiJsLib = ProjectRef(file("../lib"), "apiJS")
lazy val uiJsLib = ProjectRef(file("../lib"), "ui")

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      scalacOptions ++= Seq(
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      )
    )

lazy val common = (crossProject(JSPlatform, JVMPlatform) in file("common"))
  .configure(baseSettings)
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .settings(
    libraryDependencies ++= Seq()
  )
  .jvmSettings(
    Test / fork := true,
    libraryDependencies ++= Seq()
  )
  .jsSettings(
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq("io.github.cquiroz" %%% "scala-java-time" % javaTimeVersion),
    Compile / npmDependencies ++= Seq()
  )

// shared apis
lazy val api = (crossProject(JSPlatform, JVMPlatform) in file("api"))
  .dependsOn(common)
  .configure(baseSettings)
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circe,
      "io.circe" %%% "circe-generic" % circe,
      "io.circe" %%% "circe-parser" % circe,
      "com.softwaremill.sttp.client3" %%% "core" % sttp
    )
  )
  .jvmSettings(
    Test / fork := true,
    libraryDependencies ++= Seq()
  )
  .jsSettings(
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(),
    Compile / npmDependencies ++= Seq()
  )

// shared on the ui only
lazy val ui = (project in file("ui"))
  .configure(baseSettings)
  .configure(_.enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .dependsOn(api.js, common.js)
  .settings(
    name := "cazadescuentos-ui",
    scalacOptions += "-Ymacro-annotations",
    Test / requireJsDomEnv := true,
    stTypescriptVersion := "3.9.3",
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("react-proxy", "@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    Compile / npmDependencies ++= Seq(
      // react
      "react" -> "16.12.0",
      "react-dom" -> "16.12.0",
      "react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
      // material
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      // others
      "history" -> "4.9.0",
      "csstype" -> "3.0.11"
    ),
    Compile / npmDevDependencies ++= Seq(
      "@types/react-dom" -> "16.9.8",
      "@types/react-router" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2",
      "@types/prop-types" -> "15.7.5",
      "@types/history" -> "4.7.9"
    ),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaDomVersion,
      "me.shadaj" %%% "slinky-web" % slinkyVersion, // core React functionality, no React DOM
      "me.shadaj" %%% "slinky-core" % slinkyVersion, // React DOM, HTML and SVG tags
      "io.github.cquiroz" %%% "scala-java-time" % javaTimeVersion,
      "org.scala-js" %%% "scala-js-macrotask-executor" % macroTaskExecutorVersion,
      "com.alexitc" %%% "sjs-material-ui-facade" % muiFacadeVersion // material-ui bindings
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    common.jvm,
    common.js,
    api.jvm,
    api.js,
    ui
  )
  .settings(
    publish := {},
    publishLocal := {}
  )
