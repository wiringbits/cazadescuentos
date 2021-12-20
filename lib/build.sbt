ThisBuild / scalaVersion := "2.13.7"
ThisBuild / organization := "net.wiringbits"

val circe = "0.14.1"
val sttp = "2.1.5"

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
    libraryDependencies ++= Seq("io.github.cquiroz" %%% "scala-java-time" % "2.3.0"),
    Compile / npmDependencies in Compile ++= Seq(
      )
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
      "com.softwaremill.sttp.client" %%% "core" % sttp
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
    Compile / npmDependencies in Compile ++= Seq(
      )
  )

// shared on the ui only
lazy val ui = (project in file("ui"))
  .configure(baseSettings)
  .configure(_.enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .dependsOn(api.js, common.js)
  .settings(
    name := "cazadescuentos-ui",
    scalacOptions += "-Ymacro-annotations",
    requireJsDomEnv in Test := true,
    stTypescriptVersion := "3.9.3",
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("react-proxy", "@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "@types/react" -> "16.9.42",
      "@types/react-dom" -> "16.9.8",
      "csstype" -> "2.6.11",
      "@types/prop-types" -> "15.7.3",
      "react-proxy" -> "1.1.8"
    ),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "@types/classnames" -> "2.2.10",
      "react-router" -> "5.1.2",
      "@types/react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2"
    ),
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
      "com.alexitc" %%% "sjs-material-ui-facade" % "0.1.5"
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
