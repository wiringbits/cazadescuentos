package net.wiringbits.cazadescuentos.components.widgets

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
import net.wiringbits.cazadescuentos.api.http.models.GetTrackedProductsResponse
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{Loader, ProductCard, RemoteDataLoader}
import net.wiringbits.cazadescuentos.ui.hooks.GenericHooks
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

import scala.concurrent.ExecutionContext.Implicits.global

@react object ProductList {
  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (timesReloadingData, forceRefresh) = GenericHooks.useForceRefresh

    def onDelete(item: GetTrackedProductsResponse.TrackedProduct): Unit = {
      props.api.productService.delete(props.appInfo.buyerId, item.storeProduct).foreach { _ =>
        forceRefresh()
      }
    }

    RemoteDataLoader[GetTrackedProductsResponse](
      fetch = () => props.api.productService.getAllSummaryV2(props.appInfo.buyerId),
      render = response => {
        response.data map { item =>
          ProductCard(item, onDelete = () => onDelete(item)).withKey(item.id.toString)
        }
      },
      progressIndicator = () => Loader(),
      watchedObjects = List(timesReloadingData)
    )
  }
}
