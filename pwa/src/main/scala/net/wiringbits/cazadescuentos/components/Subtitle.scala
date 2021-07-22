package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.csstype.mod.{FlexWrapProperty, TextAlignProperty}
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
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object Subtitle {
  case class Props(text: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "subtitle" -> CSSProperties()
          .setTextAlign(TextAlignProperty.center)
          .setMargin("0 0 16px 0")
          .setFontSize("2em")
          .set("font-wright", "500")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    mui
      .Typography()
      .className(classes("subtitle"))(props.text)
  }
}
