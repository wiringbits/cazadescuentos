import net.cazadescuentos._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  private val config = if (net.cazadescuentos.BuildInfo.production) {
    Config.Default
  } else {
    Config.Dev
  }

  def main(args: Array[String]): Unit = {
    // the main shouldn't do anything to avoid conflicts between contexts (tab, popup, background)
  }

  @JSExportTopLevel("runOnTab")
  def runOnTab(): Unit = {
    activetab.Runner(config).run()
  }

  @JSExportTopLevel("runOnBackground")
  def runOnBackground(): Unit = {
    background.Runner(config).run()
  }

  @JSExportTopLevel("runOnPopup")
  def runOnPopup(): Unit = {
    popup.Runner(config.serverUrl).run()
  }
}
