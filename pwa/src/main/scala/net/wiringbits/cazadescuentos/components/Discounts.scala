package net.wiringbits.cazadescuentos.components

import slinky.core.annotations.react
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
import net.wiringbits.cazadescuentos.components.Discounts.DiscountStatus.DiscountStatus
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{DiscountCard, ErrorMessage, Loader}
import net.wiringbits.cazadescuentos.ui.hooks.MediaQueryHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html._

import scala.concurrent.ExecutionContext.Implicits.global
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

    def setData(data: Option[List[Discount]]): Unit = {
      setState(state.copy(data = data, status = DiscountStatus.Done))
    }

    def setError(ex: Throwable): Unit = {
      setState(state.copy(error = Some(ex), status = DiscountStatus.Error))
    }

    def fechData(): Unit = {
      setState(state.copy(status = DiscountStatus.Loading, error = None))
      props.api.productService.bestDiscounts(props.appInfo.buyerId).onComplete {
        case Success(res) => setData(Some(res.data))
        case Failure(ex) => setError(ex)
      }
    }

    def renderItems(items: List[Discount]) = {
      if (props.filterText.isEmpty) {
        items.map { item =>
          DiscountCard(item).withKey(item.id.toString)
        }
      } else {
        items.withFilter(x => x.name.toLowerCase contains props.filterText.toLowerCase).map { item =>
          DiscountCard(item).withKey(item.id.toString)
        }
      }
    }

    def render(items: List[Discount]) = {
      if (isMobileOrTablet) {
        div(className := classes("discounts"))(mui.List()(renderItems(items)))
      } else {
        div(className := classes("discounts"))(div(className := classes("itemsGrid"))(renderItems(items)))
      }
    }

    Hooks.useEffect(fechData, List(0))

    state.status match {
      case DiscountStatus.Initial => div("none")
      case DiscountStatus.Error =>
        ErrorMessage(
          message = state.error.toString,
          buttonText = Some("Reintentar"),
          onButtonClick = Some(() => {
            println("click")
          })
        )
      case DiscountStatus.Loading => Loader()
      case DiscountStatus.Done =>
        state.data match {
          case None => div("empty")
          case Some(items) => render(items)
        }
    }
  }
}
