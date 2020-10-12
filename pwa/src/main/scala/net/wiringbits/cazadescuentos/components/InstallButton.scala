package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import org.scalajs.dom.Event
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._
import typings.materialUiCore.createMuiThemeMod.Theme
import typings.materialUiCore.mod.PropTypes
import typings.materialUiCore.{components => mui}
import typings.materialUiIcons.{components => muiIcons}
import typings.materialUiStyles.makeStylesMod.StylesHook
import typings.materialUiStyles.mod.makeStyles
import typings.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

@react object InstallButton {
  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "fab" -> CSSProperties().setMargin(theme.spacing.unit)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { _ =>
    val (deferredInstallPrompt, setDeferredInstallPrompt) = Hooks.useState(Option.empty[Event])

    def handleInstall(): Unit = {
      // Show install prompt & hide the install button.
      deferredInstallPrompt.foreach { e =>
        val event = e.asInstanceOf[js.Dynamic]
        event.prompt()
        event.userChoice.asInstanceOf[js.Promise[js.Dynamic]].toFuture.foreach { choice =>
          println(s"User choice: ${choice.outcome}") // accepted / dismissed
        }
        setDeferredInstallPrompt(None)
      }
    }

    def handleBeforeInstallEvent(e: Event): Unit = {
      setDeferredInstallPrompt(Option(e))
    }

    def handleAppInstalled(e: Event): Unit = {
      println("Got appInstalled event")
      dom.console.log(e)
    }

    Hooks.useEffect(() => {
      dom.window.addEventListener("beforeinstallprompt", handleBeforeInstallEvent)
      dom.window.addEventListener("appinstalled", handleAppInstalled);
    }, "")

    val classes = useStyles(())

    deferredInstallPrompt match {
      case Some(_) =>
        div(
          mui
            .Button()
            .className(classes("fab"))
            .color(PropTypes.Color.primary)
            .`aria-label`("Instalar")
            .onClick(_ => handleInstall())(muiIcons.CloudDownload(), "Instalar")
        )

      case None => div()
    }
  }
}
