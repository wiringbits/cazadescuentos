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
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
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
import slinky.core.facade.Fragment
import slinky.web.html._

@react object ErrorMessage {
  case class Props(message: String, buttonText: Option[String] = None, onButtonClick: Option[() => Unit] = None)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "errorMessage" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setJustifyContent("center")
          .setAlignSelf("center")
          .setJustifySelf("center")
          .setPadding(32)
          .setBorderRadius(8),
        "errorMessageIcon" -> CSSProperties()
          .setFontSize("4em"),
        "errorMessageButton" -> CSSProperties()
          .setMarginTop(24)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val button = props.buttonText match {
      case Some(text) =>
        Fragment(
          mui
            .Button(className := classes("errorMessageButton"))(text)
            .color(Color.secondary)
            .onClick(_ => {
              props.onButtonClick match {
                case Some(cb) => cb()
                case None =>
              }
            })
        )
      case None => Fragment()
    }

    mui.Paper(className := classes("errorMessage"))(
      mui
        .Icon(className := classes("errorMessageIcon"))(
          muiIcons.Info().fontSize(muiStrings.inherit)
        )
        .color(Color.primary),
      mui.Typography()("Ocurrió un problema inesperado").variant(muiStrings.h6),
      mui.Typography()(props.message).variant(muiStrings.body2),
      button
    )
  }
}
