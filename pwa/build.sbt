import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

val circe = "0.14.1"
val sttp = "2.1.5"

lazy val commonJsLib = ProjectRef(file("../lib"), "commonJS")
lazy val apiJsLib = ProjectRef(file("../lib"), "apiJS")
lazy val uiJsLib = ProjectRef(file("../lib"), "ui")

/**
 * Say just `build` or `sbt build` to make a production bundle in `build`
 */
lazy val build = TaskKey[File]("build")

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      name := "cazadescuentos-app",
      organization := "net.wiringbits",
      scalaVersion := "2.13.7",
      scalacOptions ++= Seq(
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      ),
      scalaJSUseMainModuleInitializer := true,
      /* disabled because it somehow triggers many warnings */
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
      /* for slinky */
      libraryDependencies ++= Seq("me.shadaj" %%% "slinky-hot" % "0.7.0"),
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
        "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.3.0"
      ),
      scalacOptions += "-Ymacro-annotations",
      fork in Test := true,
      requireJsDomEnv in Test := true
    )

/**
 * Implement the  `build` task define above.
 * Most of this is really just to copy the index.html file around.
 */
lazy val browserProject: Project => Project =
  _.settings(
    build := {
      val artifacts = (Compile / fullOptJS / webpack).value
      val artifactFolder = (Compile / fullOptJS / crossTarget).value
      val jsFolder = (ThisBuild / baseDirectory).value / "src" / "main" / "js"
      val distFolder = (ThisBuild / baseDirectory).value / "build"

      distFolder.mkdirs()
      artifacts.foreach { artifact =>
        val target = artifact.data.relativeTo(artifactFolder) match {
          case None => distFolder / artifact.data.name
          case Some(relFile) => distFolder / relFile.toString
        }

        Files.copy(artifact.data.toPath, target.toPath, REPLACE_EXISTING)
      }

      // copy public resources
      Files
        .walk(jsFolder.toPath)
        .filter(x => !Files.isDirectory(x))
        .forEach(
          source => {
            source.toFile.relativeTo(jsFolder).foreach { relativeSource =>
              val dest = distFolder / relativeSource.toString
              dest.getParentFile.mkdirs()
              Files.copy(source, dest.toPath, REPLACE_EXISTING)
            }
          }
        )

      // link the proper js bundle
      val indexFrom = baseDirectory.value / "src/main/js/index.html"
      val indexTo = distFolder / "index.html"

      val indexPatchedContent = {
        import collection.JavaConverters._
        Files
          .readAllLines(indexFrom.toPath, IO.utf8)
          .asScala
          .map(_.replaceAllLiterally("-fastopt-", "-opt-"))
          .mkString("\n")
      }

      Files.write(indexTo.toPath, indexPatchedContent.getBytes(IO.utf8))
      distFolder
    }
  )

// specify versions for all of reacts dependencies
lazy val reactNpmDeps: Project => Project =
  _.settings(
    stTypescriptVersion := "3.9.3",
    stIgnore += "react-proxy",
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "@types/react" -> "16.9.42",
      "@types/react-dom" -> "16.9.8",
      "csstype" -> "2.6.11",
      "@types/prop-types" -> "15.7.3",
      "react-proxy" -> "1.1.8"
    )
  )

lazy val withCssLoading: Project => Project =
  _.settings(
    /* custom webpack file to include css */
    webpackConfigFile := Some((ThisBuild / baseDirectory).value / "custom.webpack.config.js"),
    Compile / npmDevDependencies ++= Seq(
      "webpack-merge" -> "4.2.2",
      "css-loader" -> "3.4.2",
      "style-loader" -> "1.1.3",
      "file-loader" -> "5.1.0",
      "url-loader" -> "3.0.0"
    )
  )

lazy val bundlerSettings: Project => Project =
  _.settings(
    Compile / fastOptJS / webpackExtraArgs += "--mode=development",
    Compile / fullOptJS / webpackExtraArgs += "--mode=production",
    Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",
    Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production"
  )

lazy val buildInfoSettings: Project => Project = _.enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoKeys ++= Seq[BuildInfoKey](
      "production" -> (sys.env.getOrElse("PROD", "false") == "true")
    ),
    buildInfoPackage := "net.wiringbits.cazadescuentos",
    buildInfoUsePackageAsPath := true
  )

lazy val root = (project in file("."))
  .dependsOn(commonJsLib, apiJsLib, uiJsLib)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .configure(baseSettings, browserProject, reactNpmDeps, withCssLoading, bundlerSettings, buildInfoSettings)
  .settings(
    useYarn := true,
    webpackDevServerPort := 8080,
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "recharts" -> "1.8.5",
      "@types/recharts" -> "1.8.10",
      "@types/classnames" -> "2.2.10",
      "react-router" -> "5.1.2",
      "@types/react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2",
      "detect-browser" -> "5.2.0",
      "i18next" -> "19.8.4",
      "i18next-browser-languagedetector" -> "6.0.1",
      "react-i18next" -> "11.8.5"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circe,
      "io.circe" %%% "circe-generic" % circe,
      "io.circe" %%% "circe-parser" % circe,
      "com.softwaremill.sttp.client" %%% "core" % sttp,
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
      "com.alexitc" %%% "sjs-material-ui-facade" % "0.1.5"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.1.4" % Test
    )
  )

addCommandAlias("dev", ";fastOptJS::startWebpackDevServer;~fastOptJS")
