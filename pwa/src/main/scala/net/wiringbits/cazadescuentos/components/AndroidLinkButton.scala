package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, ObjectFitProperty, PositionProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.ui.components.DiscountCard.imgSize
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object AndroidLinkButton {
  type Props = Unit

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "androidLinkButton" -> CSSProperties()
          .setDisplay("block")
          .setMargin("16px auto"),
        "img" -> CSSProperties()
          .setWidth("auto")
          .setHeight(64)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  private val url = "https://play.google.com/store/apps/details?id=net.cazadescuentos.app"
  private val imgUrl =
    "https://camo.githubusercontent.com/f587fa047a9b625fedbe5304fce1179255274f73/68747470733a2f2f6769746875622e6769746875626173736574732e636f6d2f696d616765732f6d6f64756c65732f64617368626f6172642f676f6f676c652d706c61792d62616467652e706e67"

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    a(className := classes("androidLinkButton"), href := url, target := "_blank")(
      img(className := classes("img"), src := imgUrl)
    )
  }
}
