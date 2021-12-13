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
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse.Discount
import net.wiringbits.cazadescuentos.components.widgets.Discounts.DiscountStatus.DiscountStatus
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{DiscountCard, ErrorMessage, Loader, RemoteDataLoader}
import net.wiringbits.cazadescuentos.ui.hooks.{GenericHooks, MediaQueryHooks}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import scala.util.{Failure, Success}

@react object Discounts {
  case class Props(api: API, appInfo: AppInfo, filterText: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "discounts" -> CSSProperties()
          .setFlex("auto")
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("center"),
        "itemsGrid" -> CSSProperties()
          .setDisplay("grid")
          .setGridTemplateColumns("auto auto")
          .setGridColumnGap(16)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  object DiscountStatus extends Enumeration {
    type DiscountStatus = Value
    val Initial, Loading, Error, Done = Value
  }

  private case class State(
      status: DiscountStatus,
      data: Option[List[Discount]],
      error: Option[Throwable]
  )
  private val initialState = State(DiscountStatus.Initial, None, None)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (state, setState) = Hooks.useState(initialState)
    val isMobileOrTablet = MediaQueryHooks.useIsMobileOrTablet()
    val (timesReloadingData, forceRefresh) = GenericHooks.useForceRefresh

    RemoteDataLoader[GetDiscountsResponse](
      fetch = () => props.api.productService.bestDiscounts(props.appInfo.buyerId),
      render = response => {
        response.data map { item =>
          DiscountCard(item).withKey(item.id.toString)
        }
      },
      progressIndicator = () => Loader(),
      watchedObjects = List(timesReloadingData)
    )
  }
}
