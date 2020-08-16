# The chrome extension
Running `sbt chromePackage` on this project is enough to package the extension, and test it locally, where you can take:
- `target/chrome/cazadescuentos.zip` for Chrome.
- 'target/chrome/unpacked-opt/' for Firefox.

You will need to install [sbt](https://www.scala-sbt.org/), and, ideally, [nvm](https://github.com/nvm-sh/nvm).

## Development
- Running `sbt "~chromeUnpackedFast"` will build the app each time it detects changes on the code, it also disables js optimizations which result in faster builds (placing the build at `target/chrome/unpacked-fast`).
- Be sure to integrate scalafmt on your IntelliJ to format the source code on save (see https://scalameta.org/scalafmt/docs/installation.html#intellij).

## Release
You will need the `web-ext` tool installed, which cab be done by running `npm i -g web-ext`

Run: `./dist.sh` which generates the necessary files to upload to the Firefox/Chrome stores (the `web-ext-artifacts` folder).

## Docs
The project has 3 components, and each of them acts as a different application, there are interfaces for interacting between them.

### Active Tab
The [activetab](/src/main/scala/net/cazadescuentos/activetab) package has the script that is executed when the user visits a web page that the app is allowed to interact with (like liverpool), everything running on the active tab has limited permissions, see the [official docs](https://developer.chrome.com/extensions/activeTab) for more details, be sure to read about the [content scripts](https://developer.chrome.com/extensions/content_scripts) too.

### Popup
The [activetab](/src/main/scala/net/cazadescuentos/popup) package has the script that is executed when the user visits clicks on the app icon which is displayed on the browser navigation bar.
 
There is a limited functionality that the popup can do, see the [official docs](https://developer.chrome.com/extensions/browserAction) for more details.

### Background
The [background](/src/main/scala/net/cazadescuentos/background) package has the script that is executed when the browser starts, it keeps running to do anything the extension is supposed to do on the background, for example, interacts with the storage, web services, and alarms.

As other components can't interact directly with the storage or web services, they use a high-level API ([BackgroundAPI](/src/main/scala/net/cazadescuentos/background/BackgroundAPI.scala)) to request the background to do that.

Be sure to review the [official docs](https://developer.chrome.com/extensions/background_pages).


## Hints
While scalajs works great, there are some limitations, in particular, you must pay lots of attention while dealing with boundaries.

A boundary is whatever interacts directly with JavaScript, like the browser APIs, for example:
- When the background receives requests from other components, it gets a message that is serialized and deserialized by the browser, in order to support strongly typed models, the app encodes these models to a JSON string, the browser knows how to deal with strings but it doesn't know what to do with Scala objects.
- When the storage needs to persist data, the app encodes the Scala objects to a JSON string, which is what the browser understands.
- When there is a need to interact with JavaScript APIs (like external libraries), you'll need to write a [facade](/src/main/scala/net/cazadescuentos/facades) unless there is one available, this facade will deal with primitive types only (strings, ints, etc).
