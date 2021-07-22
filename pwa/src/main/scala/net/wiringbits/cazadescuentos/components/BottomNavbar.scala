package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.mod._
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade._
import typings.reactRouterDom.mod.useHistory

@react object BottomNavbar {
  type Props = Unit

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "bottomNavbar" -> CSSProperties()
          .setMaxWidth(480)
          .setOverflow("hidden")
          .setBorderRadius(32)
          .setMargin("0 auto"),
        "nav" -> CSSProperties()
          .setDisplay("flex")
          .setJustifyContent("space-evenly"),
        "button" -> CSSProperties()
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val history = useHistory()
    val pathname = dom.window.location.pathname

    val (routeValue, setRoute) = Hooks.useState(pathname)

    def handleOnChange(value: Any): Unit = {
      setRoute(value.toString)
      history.push(value.toString)
    }

    mui
      .Paper()
      .className(classes("bottomNavbar"))(
        mui
          .BottomNavigation()
          .className(classes("nav"))
          .component("nav")
          .value(routeValue)
          .onChange((_, value) => handleOnChange(value))
          .showLabels(true)(
            button("/", "Inicio", muiIcons.LocalOffer()),
            button("/buenfin", "Buen Fin", muiIcons.CalendarToday()),
            button("/guia", "Ayuda", muiIcons.Help())
          )
      )
  }

  private def button(path: String, title: String, icon: ReactElement) = {
    mui
      .BottomNavigationAction()
      .value(path)
      .label(title)
      .iconReactElement(icon)
      .color("green")
  }

}
