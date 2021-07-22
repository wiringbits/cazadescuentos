package net.wiringbits.cazadescuentos.ui.components

import com.alexitc.materialui.facade.csstype.mod._
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{
  typographyTypographyMod,
  components => mui,
  materialUiCoreStrings => muiStrings
}
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
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object EmptySummary {
  case class Props(logo: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "img" -> CSSProperties()
          .setWidth("auto")
          .setHeight(64)
          .setObjectFit(ObjectFitProperty.contain),
        "content" -> CSSProperties()
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

    mui.Card(
      mui.CardContent.className(classes("content"))(
        img(className := classes("img"), src := props.logo, alt := "logo"),
        mui
          .Typography()
          .variant(typographyTypographyMod.Style.h5)("No hay productos"),
        mui
          .Typography()
          .variant(typographyTypographyMod.Style.body1)("Agrega algunos productos para ayudarte a buscar descuentos")
      )
    )
  }
}
