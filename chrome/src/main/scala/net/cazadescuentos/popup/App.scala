package net.cazadescuentos.popup

import net.cazadescuentos.popup.components.HomeComponent
import net.wiringbits.cazadescuentos.ui.components.Notifications
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html.div
import typings.materialUiCore.createMuiThemeMod.{Theme, ThemeOptions}
import typings.materialUiCore.createTypographyMod.TypographyOptions
import typings.materialUiCore.stylesMod.createMuiTheme
import typings.materialUiCore.{components => mui}
import typings.materialUiStyles.components.ThemeProvider
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.{components => router}

@react
object App {

  val theme: Theme = createMuiTheme(
    ThemeOptions()
      .setTypography(
        TypographyOptions().setUseNextVariants(true)
      ) // https://v3.material-ui.com/style/typography/#migration-to-typography-v2
  )

  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ThemeProvider(theme)(
      mui.CssBaseline(),
      router.BrowserRouter.basename("")(
        router.Switch(
          router.Route(
            RouteProps()
              .setRender { _ =>
                div(
                  Notifications.component(
                    Notifications.Props(
                      texts = new Notifications.Texts {
                        override val retry = props.api.messages.labelRetry
                      },
                      buyerId = props.appInfo.buyerId,
                      httpService = props.api.productHttpService
                    )
                  ),
                  HomeComponent.component(HomeComponent.Props(props.api, props.appInfo))
                )
              }
          )
        )
      )
    )
  }
}
