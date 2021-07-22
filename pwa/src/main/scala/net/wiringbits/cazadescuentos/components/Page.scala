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
import net.wiringbits.cazadescuentos.components.widgets.BottomNavbar
import org.scalablytyped.runtime.StringDictionary
import slinky.core._
import slinky.web.html._
import slinky.core.annotations._
import slinky.core.facade._
import typings.history.mod.LocationState
import typings.reactRouter.mod.{RouteComponentProps, StaticContext}

@react object Page {
  case class Props(route: RouteComponentProps[_, StaticContext, LocationState], child: ReactElement)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "page" -> CSSProperties()
          .setMinWidth("100%")
          .setMinHeight("100vh")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setPadding("24px 24px 96px 24px"),
        "bgn" -> CSSProperties()
          .setPosition(PositionProperty.absolute)
          .setWidth("100%")
          .setHeight("300px")
          .setBorderRadius(0)
          .setBackgroundColor("#e74c3c")
          .setBackgroundPosition("center")
          .setBackgroundSize("cover")
          .setBackground("url('images/shapes/page_bgn_shape.svg')"),
        "body" -> CSSProperties()
          .setFlex("auto")
          .setPosition(PositionProperty.relative)
          .setOverflow("visible")
          .setMaxWidth(1024)
          .setMargin("0 auto")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column),
        "nav" -> CSSProperties()
          .setPosition(PositionProperty.fixed)
          .setBottom(16)
          .setRight(16)
          .setLeft(16)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    Fragment(
      mui.Paper(className := classes("bgn")).elevation(0),
      div(
        className := classes("page"),
        div(className := classes("body"), props.child)
      ),
      div(className := classes("nav"), BottomNavbar())
    )
  }
}
