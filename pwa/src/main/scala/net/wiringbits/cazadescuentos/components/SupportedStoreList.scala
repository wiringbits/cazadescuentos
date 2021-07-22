package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.detectBrowser.mod.Browser
import com.alexitc.materialui.facade.materialUiCore.{
  typographyTypographyMod,
  components => mui,
  materialUiCoreStrings => muiStrings
}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.ui.components.{TextLine, YoutubeVideo}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.facade.Fragment

@react object SupportedStoreList {
  case class Props(title: String, stores: List[String])

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "supportedStoreList" -> CSSProperties()
          .setMargin("16px 0"),
        "itemText" -> CSSProperties()
          .setColor("#1565C0")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    def goToStore(store: String): Unit = { dom.window.open(store, "_blank") }

    val items = props.stores.map { store =>
      mui
        .ListItem()(
          mui
            .Typography(className := classes("itemText"))(store)
            .variant(muiStrings.body2)
        )
        .button(true)
        .divider(true)
        .dense(true)
        .onClick(_ => goToStore(store))
        .withKey(store)
    }

    div(className := classes("supportedStoreList"))(
      mui.Typography()(props.title).variant(muiStrings.h6),
      mui.List()(items)
    )
  }
}
