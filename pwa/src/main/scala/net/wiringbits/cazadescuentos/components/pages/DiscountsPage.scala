package net.wiringbits.cazadescuentos.components.pages

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
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse.Discount
import net.wiringbits.cazadescuentos.components.widgets.{Discounts, SearchInput}
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{Scaffold, ScaffoldAppBar}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html._
import typings.reactI18next.mod.useTranslation

import scala.scalajs.js

@react object DiscountsPage {
  case class Props(api: API, appInfo: AppInfo)
  private case class State(searchText: String, data: Option[List[Discount]])
  private val initialState = State("", None)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val js.Tuple3(t, _, _) = useTranslation()
    val (state, setState) = Hooks.useState(initialState)

    def setSearchText(searchText: String): Unit = {
      setState(state.copy(searchText = searchText))
    }

    Scaffold(
      Discounts(props.api, props.appInfo, state.searchText),
      appBar = Some(
        ScaffoldAppBar(
          t("discounts").toString,
          Some(
            SearchInput(initialValue = initialState.searchText, onChange = setSearchText)
          )
        )
      )
    )
  }
}
