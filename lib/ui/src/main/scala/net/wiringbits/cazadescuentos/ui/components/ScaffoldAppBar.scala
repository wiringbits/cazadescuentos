package net.wiringbits.cazadescuentos.ui.components

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
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html._

@react object ScaffoldAppBar {
  case class Props(title: String, child: Option[ReactElement] = None)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "scaffoldAppBar" -> CSSProperties()
          .setColor("#FFF")
          .setBackground(
            "radial-gradient(circle, rgba(227,63,51,1) 0%, rgba(244,67,54,1) 90%, rgba(244,67,54,0.9) 100%)"
          )
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center")
          .setPadding("16px 0"),
        "scaffoldAppBarTitle" -> CSSProperties()
          .setHeight(32)
          .setColor("#FFF")
          .setDisplay("flex")
          .setAlignItems("center"),
        "scaffoldAppBarChild" -> CSSProperties()
          .setMargin(16)
          .setWidth("100%")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val title = mui
      .Typography(props.title)
      .className(classes("scaffoldAppBarTitle"))
      .variant(muiStrings.h5)

    val child = props.child match {
      case Some(child) => Fragment(div(className := classes("scaffoldAppBarChild"))(child))
      case None => Fragment()
    }

    mui.Paper(className := classes("scaffoldAppBar"))(
      title,
      child
    )
  }
}
