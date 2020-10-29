package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.api.http.models.GetTrackedProductsResponse
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.components.models.DataState
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.MyProductsSummaryComponent
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._
import typings.csstype.csstypeStrings.auto
import typings.detectBrowser.mod.Browser
import typings.materialUiCore.createMuiThemeMod.Theme
import typings.materialUiCore.{typographyTypographyMod, components => mui}
import typings.materialUiStyles.makeStylesMod.StylesHook
import typings.materialUiStyles.mod.makeStyles
import typings.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}

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

  type Data = List[GetTrackedProductsResponse.TrackedProduct]
  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (dataState, setDataState) = Hooks.useState(DataState.loading[Data])

    def refreshData(): Unit = {
      setDataState(DataState.loading[Data])
      props.api.productService.getAllSummaryV2().onComplete {
        case Success(response) => setDataState(_.loaded(response.data))
        case Failure(exception) => setDataState(_.failed(exception.getMessage))
      }
    }

    def delete(item: StoreProduct): Unit = {
      setDataState(
        cur =>
          cur match {
            case DataState.Loaded(data) =>
              val newData = data.filter(_.storeProduct != item)
              DataState.Loaded(newData)
            case x => x
          }
      )
      props.api.productService.delete(item)
    }

    Hooks.useEffect(refreshData, "")

    val classes = useStyles(())
    val data = dataState match {
      case DataState.Loading() =>
        div(
          mui.CircularProgress()
        )

      case DataState.Failed(msg) =>
        div(
          mui
            .Typography()
            .color(typings.materialUiCore.mod.PropTypes.Color.secondary)
            .variant(typographyTypographyMod.Style.h6)(msg)
        )

      case DataState.Loaded(data) =>
        div(
          AddNewItemFloatingButton.component(AddNewItemFloatingButton.Props(props.api, refreshData)),
          MyProductsSummaryComponent.component(MyProductsSummaryComponent.Props(data, delete))
        )
    }

    val installButton = if (props.appInfo.isAndroidApp) {
      div()
    } else {
      div(
        InstallButton.component(InstallButton.Props(props.api))
      )
    }

    val notificationsUnsupported = props.appInfo.browser.exists { browser =>
      List(Browser.safari, Browser.ios, Browser.`ios-webview`).contains(browser.name)
    }
    val disclaimer = if (notificationsUnsupported) {
      """Desafortunadamente las notificaciones no funcionan en iPhone/Safari todavia.
        |
        |Agradecemos tu paciencia, recuerda que si quieres usar la versión completa de la aplicación,
        |esta puede ser instalada en tu computadora desde https://cazadescuentos.net
        |""".stripMargin
    } else {
      """Recuerda que si quieres usar la versión completa de la aplicación,
        |esta puede ser instalada en tu computadora desde https://cazadescuentos.net
        |""".stripMargin
    }

    mui.Paper.className(classes("root"))(
      data,
      installButton,
      mui
        .Typography()
        .component("p")
        .variant(typographyTypographyMod.Style.h6)(disclaimer)
    )
  }
}
