package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, FlexWrapProperty, TextAlignProperty}
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
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse.Discount
import net.wiringbits.cazadescuentos.common.models.OnlineStore
import net.wiringbits.cazadescuentos.components.widgets.{Discounts, SearchInput}
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{DiscountCard, MessageCard, Title}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html._

@react object DiscountsComponent {
  case class Props(api: API, appInfo: AppInfo)

  private case class State(searchText: String, data: Option[List[Discount]])
  private val initialState = State("", None)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "discountsComponent" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (state, setState) = Hooks.useState(initialState)

    def setSearchText(searchText: String): Unit = {
      setState(state.copy(searchText = searchText))
    }

    div(className := classes("discountsComponent"))(
      Title("Buen Fin"),
      SearchInput(initialValue = initialState.searchText, onChange = setSearchText),
      MessageCard("Mira los mejores descuentos que hemos encontrado en la ultima semana"),
      Discounts(props.api, props.appInfo, state.searchText)
    )
  }
}
