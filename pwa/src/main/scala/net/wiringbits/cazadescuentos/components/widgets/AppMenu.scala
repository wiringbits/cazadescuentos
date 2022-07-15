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
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._
import typings.reactI18next.mod.useTranslation
import typings.reactRouterDom.mod.useHistory

import scala.scalajs.js
import scala.scalajs.js.timers.RawTimers.setTimeout

@react object AppMenu {
  type Props = Unit

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "appMenu" -> CSSProperties()
          .setPosition(PositionProperty.fixed)
          .setBottom(0)
          .setLeft(0)
          .setRight(0)
          .setZIndex(10)
          .setDisplay("flex"),
        "appMenuNavContainer" -> CSSProperties()
          .setWidth("100%")
          .setMaxWidth(300)
          .setMargin("16px auto")
          .setBorderRadius(32)
          .set("overflow", "hidden"),
        "appMenuNav" -> CSSProperties()
          .setHeight(64)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val js.Tuple3(t, _, _) = useTranslation()
    val classes = useStyles(())
    val history = useHistory()
    val (path, setPath) = Hooks.useState("/")
    val (visible, setVisible) = Hooks.useState(false)

    Hooks.useEffect(
      () => {
        if (!visible) {
          setTimeout(
            () => {
              setVisible(true)
            },
            500
          )
        }
      },
      ""
    )

    val body = div(className := classes("appMenu"))(
      mui.Paper(className := classes("appMenuNavContainer"))(
        mui
          .BottomNavigation(className := classes("appMenuNav"))(
            mui
              .BottomNavigationAction()
              .iconReactElement(mui.Icon(muiIcons.LocalOffer()))
              .label(t("home"))
              .showLabel(true)
              .value("/"),
            mui
              .BottomNavigationAction()
              .iconReactElement(mui.Icon(muiIcons.ShoppingCart()))
              .label(t("discounts"))
              .showLabel(true)
              .value("/buenfin"),
            mui
              .BottomNavigationAction()
              .iconReactElement(mui.Icon(muiIcons.Help()))
              .label(t("help"))
              .showLabel(true)
              .value("/guia")
          )
          .value(path)
          .onChange((evt, value) => {
            setPath(value.toString)
            history.push(value.toString)
          })
      )
    )

    mui
      .Slide(muiStrings.up)(body)
      .in(visible)
      .timeout(300)
  }
}
