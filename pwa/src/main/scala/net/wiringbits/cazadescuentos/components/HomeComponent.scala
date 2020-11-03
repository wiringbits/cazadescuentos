package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.api.http.models.GetTrackedProductsResponse
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{MyProductsSummaryComponent, RemoteDataLoaderBase}
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

@react object MyProductsSummaryLoader extends RemoteDataLoaderBase[GetTrackedProductsResponse] {
  val component = theComponent
}

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
    val (refreshTime, refresh) = Hooks.useReducer[Int, Int](_ + _, 0)
    def delete(item: StoreProduct): Unit = {
      props.api.productService.delete(item).foreach { _ =>
        refresh(1)
      }
    }

    val classes = useStyles(())
    val data = MyProductsSummaryLoader.component(
      MyProductsSummaryLoader.Props(
        fetcher = () => props.api.productService.getAllSummaryV2(),
        render = data => {
          MyProductsSummaryComponent.component(
            MyProductsSummaryComponent.Props(
              data.data,
              delete,
              new MyProductsSummaryComponent.Texts {}, // TODO: i18n
              new MyProductsSummaryComponent.Icons {}
            )
          )
        },
        refreshTime
      )
    )

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
      AddNewItemFloatingButton.component(AddNewItemFloatingButton.Props(props.api, () => refresh(1))),
      data,
      installButton,
      mui
        .Typography()
        .component("p")
        .variant(typographyTypographyMod.Style.h6)(disclaimer)
    )
  }
}
