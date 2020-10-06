# Cazadescuentos App
This a PWA running on https://app.cazadescuentos.net, the intention is to be the official mobile app.

## Development
- Run `sbt dev` to launch the website on `localhost:8080`, which reloads on code changes, run `PROD=true sbt dev` to use the production server instead of the local one.
- Run `sbt build` to package the app to release, output stored at `/build`.
- Put public resources on the [js](src/main/js) folder.
- Use IntelliJ, and configure it to format the code on save with [scalafmt](https://scalameta.org/scalafmt/docs/installation.html#intellij) to keep the code consistent.
- Make sure to follow the [Slinky](https://slinky.dev/) tutorial, and enable the `Slinky` extension on IntelliJ, otherwise, you may find highlighting errors while sbt compiles fine.
- Follow the [Material UI V3](https://v3.material-ui.com/) docs to understand the components the app is using (V4 is not supported yet by Slinky).
- Follow the [Material UI Slinky Demo](https://github.com/ScalablyTyped/SlinkyDemos/tree/master/material-ui/) when necessary.
- Follow the [Scala.js for JavaScript developers](https://www.scala-js.org/doc/sjs-for-js/) tutorial to get an understanding on how the project works.

### Hints
It's strongly recommended to use these imports while dealing with material-ui components instead of referencing the components directly, otherwise, IntelliJ gets quite slow, and tends to highlight errors while sbt compiles properly:

```scala
import typings.materialUiCore.{components => mui}
import typings.materialUiIcons.{components => muiIcons}
```

## ToDo
The app has been developed in a hurry by grabbing lots of code from the [chrome extension](https://github.com/wiringbits/cazadescuentos/tree/master/chrome), hence, there are some decisions that aren't ideal for a webapp, and potential code duplication between projects.

These are some tasks requiring to be done:
- Integrate i18n, everything is in Spanish right now.
- Extract the common pieces in a reusable component, most of it should be on the [common](src/main/scala/net/wiringbits/cazadescuentos/common) package.
- Look for discounts continuously instead of doing it once when the app starts.
- Integrate push notifications to display discounts, PWA seem to handle this quite well.
- Integrate proper notifications instead of painting an ugly text on the top of the screen.
- The UI could be far better.
- Use proper storage for webapps instead of writing everything into a single local storage key every time (is worth considering IndexedDB).
- Use a mobile friendly UI like the chrome extension does.
- Fix tests on `sbt test`.
- Fix tests on IntelliJ.
