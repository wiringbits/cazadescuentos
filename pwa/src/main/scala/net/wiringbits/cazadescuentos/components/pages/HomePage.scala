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
import net.wiringbits.cazadescuentos.components.AddNewItemFloatingButton
import net.wiringbits.cazadescuentos.components.widgets.{InstallCard, ProductList}
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{Scaffold, ScaffoldAppBar}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html._
import typings.reactI18next.mod.useTranslation

import scala.scalajs.js

@react object HomePage {
  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val js.Tuple3(t, _, _) = useTranslation()

    Scaffold(
      appBar = Some(
        ScaffoldAppBar(
          t("home").toString,
          Some(InstallCard()) // TODO: Finish Install Card
        )
      ),
      child = Fragment(
        ProductList(props.api, props.appInfo),
        AddNewItemFloatingButton(props.api, props.appInfo, () => {})
      )
    )
  }
}
