addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.3.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.18.0")

resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta29.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
