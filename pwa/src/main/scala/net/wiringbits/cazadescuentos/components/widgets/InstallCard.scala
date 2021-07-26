package net.wiringbits.cazadescuentos.components.widgets

import com.alexitc.materialui.facade.csstype.mod.{
  BoxSizingProperty,
  FlexDirectionProperty,
  FlexWrapProperty,
  ObjectFitProperty,
  PositionProperty,
  TextAlignProperty
}
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.providers.InstallPromptProvider.useInstallPrompt
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

@react object InstallCard {
  type Props = Unit

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "installCard" -> CSSProperties()
          .setMaxWidth(400)
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center")
          .setPadding(16)
          .setMargin("0 auto")
          .set("text-align", "center")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (installPrompt) = useInstallPrompt()

    dom.console.log(installPrompt.asInstanceOf[js.Dynamic])

    def handleInstall(): Unit = {
      installPrompt.foreach { e =>
        val event = e.asInstanceOf[js.Dynamic]
        event.prompt()
        event.userChoice.asInstanceOf[js.Promise[js.Dynamic]].toFuture.foreach { choice =>
          println(s"User choice: ${choice.outcome}") // accepted / dismissed
        }
//        setDeferredInstallPrompt(None)
      }
    }

    installPrompt match {
      case Some(event) =>
        mui
          .Paper(
            mui
              .Typography(
                "Para una mejor experiencia, instala la app en tu computadora desde https://cazadescuentos.net"
              )
              .variant(muiStrings.body2),
            br(),
            mui
              .Button("Instalar")
              .variant(muiStrings.contained)
              .color(muiStrings.primary)
              .onClick(_ => handleInstall())
          )
          .className(classes("installCard"))

      case None => Fragment()
    }

  }
}
