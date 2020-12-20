package net.cazadescuentos.popup.components

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.cazadescuentos.common.{I18NMessages, ResourceProvider}
import net.cazadescuentos.popup.{API, AppInfo}
import net.wiringbits.cazadescuentos.common.models.{OnlineStore, StoreProduct}
import net.wiringbits.cazadescuentos.ui.components.MyProductsSummaryComponent
import net.wiringbits.cazadescuentos.ui.hooks.GenericHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@react object HomeComponent {

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  case class Props(api: API, appInfo: AppInfo)

  private val icons = new MyProductsSummaryComponent.Icons {
    override def forStore(store: OnlineStore): String = {
      s"/img/storeLogo/${store.storeLogo}"
    }

    override def logo: String = ResourceProvider.appIcon96
  }

  private def texts(messages: I18NMessages) = new MyProductsSummaryComponent.Texts {
    override def noProducts: String = messages.dropListEmpty
    override def addItemsToFindDiscounts: String = messages.dropListEmptyDetailedMessage
    override def store: String = messages.labelStore
    override def item: String = messages.labelProduct
    override def price: String = messages.labelPrice
    override def discount: String = messages.labelDiscount
    override def available: String = messages.labelAvailable
    override def initialPrice: String = messages.labelInitialPrice
    override def actions: String = messages.labelActions
    override def remove: String = messages.labelDelete
    override def show: String = messages.labelShow
    override def priceSummary(initialPrice: String, currentPrice: String): String = {
      messages.priceSummary(initialPrice, currentPrice)
    }
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (timesReloadingData, forceRefresh) = GenericHooks.useForceRefresh
    def delete(item: StoreProduct): Unit = {
      props.api.backgroundAPI
        .deleteStoredProduct(item)
        .onComplete {
          case Success(_) => forceRefresh()
          case Failure(ex) =>
            println(s"Failed to delete item, item = $item, error = ${ex.getMessage}")
        }
    }

    val classes = useStyles(())
    val data = MyProductsSummaryDataLoader.component(
      MyProductsSummaryDataLoader.Props(
        fetch = () => props.api.productHttpService.getAllSummaryV2(props.appInfo.buyerId),
        render = data => {
          MyProductsSummaryComponent.component(
            MyProductsSummaryComponent.Props(
              data.data,
              delete,
              texts(props.api.messages),
              icons,
              disableMobileUI = true // the extension doesn't work on mobile anyway
            )
          )
        },
        retryLabel = props.api.messages.labelRetry,
        timesReloadingData = timesReloadingData
      )
    )

    mui.Paper.className(classes("root"))(
      data
    )
  }
}
