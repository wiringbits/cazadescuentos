import chrome._
import chrome.permissions.Permission
import chrome.permissions.Permission.API
import com.alexitc.{Chrome, ChromeSbtPlugin}

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val appName = "cazadescuentos"

name := appName
version := "1.27"
scalaVersion := "2.13.1"
scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-language:existentials",
  "-Xlint",
  "-deprecation",
  //"-Xfatal-warnings",
  "-feature"
)

// Enable macro annotations by setting scalac flags for Scala 2.13
scalacOptions ++= {
  import Ordering.Implicits._
  if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
    Seq("-Ymacro-annotations")
  } else {
    Nil
  }
}

enablePlugins(ChromeSbtPlugin, BuildInfoPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)

lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"

// build-info
buildInfoPackage := "net.cazadescuentos"
buildInfoKeys := Seq[BuildInfoKey](name)
buildInfoKeys ++= Seq[BuildInfoKey](
  "production" -> (sys.env.getOrElse("PROD", "false") == "true")
)

// NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
// webpack settings.
scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false)
version in webpack := "4.8.1"

webpackConfigFile := {
  val file = if (isProductionBuild) "production.webpack.config.js" else "dev.webpack.config.js"
  Some(baseDirectory.value / file)
}

// scala-js-chrome
scalaJSLinkerConfig := scalaJSLinkerConfig.value.withRelativizeSourceMapBase(
  Some((Compile / fastOptJS / artifactPath).value.toURI)
)
skip in packageJSDependencies := false

webpackBundlingMode := BundlingMode.Application

fastOptJsLib := (webpack in (Compile, fastOptJS)).value.head
fullOptJsLib := (webpack in (Compile, fullOptJS)).value.head

webpackBundlingMode := BundlingMode.LibraryAndApplication()

// you can customize and have a static output name for lib and dependencies
// instead of having the default files names like extension-fastopt.js, ...
artifactPath in (Compile, fastOptJS) := {
  (crossTarget in (Compile, fastOptJS)).value / "main.js"
}

artifactPath in (Compile, fullOptJS) := {
  (crossTarget in (Compile, fullOptJS)).value / "main.js"
}

chromeManifest := new ExtensionManifest {
  override val name = appName
  override val version = Keys.version.value

  override val description = Some(
    "Allows you to follow the items you are interested to buy if they were cheaper, notifying you once their price decreases"
  )
  require(!description.exists(_.length > 132), "Chrome allows upto 132 characters in the desciption")

  override val icons = Chrome.icons("icons", "app.png", Set(48, 96))

  override val permissions = Set[Permission](
    API.Storage,
    API.Notifications,
    API.Alarms
  )

  override val defaultLocale: Option[String] = Some("en")

  override val browserAction: Option[BrowserAction] =
    Some(BrowserAction(icons, Some(appName), Some("popup.html")))

  // scripts used on all modules
  val commonScripts = List("scripts/common.js", "main-bundle.js")

  override val background = Background(
    scripts = commonScripts ::: List("scripts/background-script.js")
  )

  override val contentScripts: List[ContentScript] = List(
    ContentScript(
      matches = List(
        "https://www.liverpool.com.mx/*",
        "https://www.zara.com/*",
        "https://www2.hm.com/*",
        "https://m2.hm.com/*",
        "https://www.coppel.com/*",
        "https://www.mercadolibre.com.mx/*",
        "https://articulo.mercadolibre.com.mx/*",
        "https://www.costco.com.mx/*",
        "https://www.costco.com/*",
        "https://www.homedepot.com/*",
        "https://www.homedepot.com.mx/*",
        "http://www.homedepot.com.mx/*",
        "https://www.ebay.com/itm/*",
        "https://www.officedepot.com.mx/*",
        "https://www.officedepot.com/*",
        "https://www.sanborns.com.mx/*",
        "https://www.sams.com.mx/*",
        "https://www.samsclub.com/*",
        "https://shop.nordstrom.com/s/*",
        "https://www.elektra.com.mx/*",
        "https://www.costco.com.mx/*",
        "https://www.costco.com/*",
        "https://www.walmart.com.mx/*",
        "https://super.walmart.com.mx/*",
        "https://www.bestbuy.com.mx/*",
        "https://www.bestbuy.com/*",
        "https://www.elpalaciodehierro.com/*",
        "https://www.amazon.com.mx/*"
      ),
      css = List("css/cazadescuentosActiveTab.css"),
      js = commonScripts ::: List("external-scripts/sidenavCzd.js", "scripts/active-tab-script.js")
    )
  )
  override val webAccessibleResources = List("icons/*")
}

val circe = "0.13.0"
val sttp = "2.1.2"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"
libraryDependencies += "com.alexitc" %%% "scala-js-chrome" % "0.7.0"

libraryDependencies += "io.circe" %%% "circe-core" % circe
libraryDependencies += "io.circe" %%% "circe-generic" % circe
libraryDependencies += "io.circe" %%% "circe-parser" % circe

libraryDependencies += "com.softwaremill.sttp.client" %%% "core" % sttp
libraryDependencies += "org.lrng.binding" %%% "html" % "1.0.3"

// js dependencies, adding typescript type definitions gets them a Scala facade
//npmDependencies in Compile ++= Seq(
//  "react" -> "16.13.1",
//  "react-dom" -> "16.13.1"
//)
