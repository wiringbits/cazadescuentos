addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.1.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.17.0")

resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta25")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
