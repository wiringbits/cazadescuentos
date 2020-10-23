ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "net.wiringbits"

val circe = "0.13.0"
val sttp = "2.1.2"

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
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-java-time" % "1.0.0"),
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
    libraryDependencies ++= Seq(),
    Compile / npmDependencies in Compile ++= Seq(
      )
  )

// shared on the ui only
lazy val ui = (project in file("ui"))
  .configure(baseSettings)
  .configure(_.enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .settings(
    name := "cazadescuentos-ui",
    libraryDependencies ++= Seq(),
    Compile / npmDependencies in Compile ++= Seq()
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
