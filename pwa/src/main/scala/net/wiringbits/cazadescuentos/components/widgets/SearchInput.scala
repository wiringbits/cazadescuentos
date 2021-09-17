package net.wiringbits.cazadescuentos.components.widgets

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.csstype.mod.{BoxSizingProperty, FlexWrapProperty, TextAlignProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{
  typographyTypographyMod,
  components => mui,
  materialUiCoreStrings => muiStrings
}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import typings.reactI18next.mod.useTranslation

import scala.scalajs.js

@react object SearchInput {
  case class Props(initialValue: String, onChange: String => Unit)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "searchInput" -> CSSProperties()
          .setWidth("100%")
          .setMaxWidth(340)
          .setHeight(48)
          .setPadding(8)
          .setMargin("8px auto")
          .setBoxSizing(BoxSizingProperty.`border-box`)
          .setBorderRadius("5em")
          .setDisplay("flex")
          .set(
            "& input",
            CSSProperties()
              .setTextAlign(TextAlignProperty.center)
          )
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val js.Tuple3(t, _, _) = useTranslation()
    val classes = useStyles(())

    val (value, setValue) = Hooks.useState(props.initialValue)

    def onValueChange(value: String): Unit = {
      setValue(value)
      props.onChange(value)
    }

    val clearButton =
      if (value.isEmpty) Fragment()
      else Fragment(mui.IconButton()(muiIcons.Clear()).onClick(_ => onValueChange("")))

    mui
      .Paper()
      .className(classes("searchInput"))(
        mui
          .Input()
          .disableUnderline(true)
          .fullWidth(true)
          .placeholder(t("search"))
          .value(value)
          .onChange(e => onValueChange(e.target.asInstanceOf[HTMLInputElement].value)),
        clearButton
      )
  }
}
