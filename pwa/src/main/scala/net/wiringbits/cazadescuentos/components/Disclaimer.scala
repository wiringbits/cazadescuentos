package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, PositionProperty, TextAlignProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{typographyTypographyMod, components => mui}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.detectBrowser.mod.Browser

@react object Disclaimer {
  case class Props(api: API, appInfo: AppInfo)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "disclaimer" -> CSSProperties()
          .setPadding(16)
          .setMargin("16px 0")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center")
          .setTextAlign(TextAlignProperty.center)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val notificationsUnsupported = props.appInfo.browser.exists { browser =>
      println(s"browser: ${browser.name}")
      List(Browser.safari, Browser.ios, Browser.`ios-webview`).contains(browser.name)
    }

    println(s"notificationsUnsupported: $notificationsUnsupported")

    val disclaimer = if (notificationsUnsupported) {
      """Desafortunadamente las notificaciones no funcionan en iPhone/Safari todavia.
        |
        |Para una mejor experiencia, instala la app en tu computadora desde https://cazadescuentos.net
        |""".stripMargin
    } else {
      "Para una mejor experiencia, instala la app en tu computadora desde https://cazadescuentos.net"
    }

    mui.Paper(className := classes("disclaimer"))(
      mui.Typography().variant(typographyTypographyMod.Style.h6)(disclaimer)
    )
  }
}
