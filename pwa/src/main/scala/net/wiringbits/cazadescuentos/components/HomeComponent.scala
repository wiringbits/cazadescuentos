package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{MyProductsSummaryComponent, Notifications}
import net.wiringbits.cazadescuentos.ui.hooks.GenericHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.csstype.csstypeStrings.auto
import typings.detectBrowser.mod.Browser
import typings.materialUiCore.createMuiThemeMod.Theme
import typings.materialUiCore.{typographyTypographyMod, components => mui}
import typings.materialUiStyles.makeStylesMod.StylesHook
import typings.materialUiStyles.mod.makeStyles
import typings.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}

import scala.concurrent.ExecutionContext.Implicits.global

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

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (timesReloadingData, forceRefresh) = GenericHooks.useForceRefresh
    def delete(item: StoreProduct): Unit = {
      props.api.productService.delete(props.appInfo.buyerId, item).foreach { _ =>
        forceRefresh()
      }
    }

    val classes = useStyles(())
    val data = MyProductsSummaryDataLoader.component(
      MyProductsSummaryDataLoader.Props(
        fetch = () => props.api.productService.getAllSummaryV2(props.appInfo.buyerId),
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
        retryLabel = "Reintentar",
        timesReloadingData = timesReloadingData
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
        |Para una mejor experiencia, instala la app en tu computadora desde https://cazadescuentos.net
        |""".stripMargin
    } else {
      """Para una mejor experiencia, instala la app en tu computadora desde https://cazadescuentos.net
        |""".stripMargin
    }

    mui.Paper.className(classes("root"))(
      Notifications.component(
        Notifications.Props(
          texts = new Notifications.Texts {
            override val retry = "Reintentar"
          },
          buyerId = props.appInfo.buyerId,
          httpService = props.api.productService
        )
      ),
      AddNewItemFloatingButton.component(AddNewItemFloatingButton.Props(props.api, props.appInfo, forceRefresh)),
      data,
      installButton,
      mui
        .Typography()
        .component("p")
        .variant(typographyTypographyMod.Style.h6)(disclaimer)
    )
  }
}
