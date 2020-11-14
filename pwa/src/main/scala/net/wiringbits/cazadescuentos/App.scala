package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.components._
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html.div
import typings.materialUiCore.createMuiThemeMod.{Theme, ThemeOptions}
import typings.materialUiCore.createTypographyMod.TypographyOptions
import typings.materialUiCore.stylesMod.createMuiTheme
import typings.materialUiCore.{components => mui}
import typings.materialUiIcons.{components => muiIcons}
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
              .setExact(true)
              .setPath("/")
              .setRender { route =>
                div(
                  menu(route.location.pathname),
                  HomeComponent.component(HomeComponent.Props(props.api, props.appInfo))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setExact(true)
              .setPath("/buenfin")
              .setRender { route =>
                div(
                  menu(route.location.pathname),
                  DiscountsComponent.component(DiscountsComponent.Props(props.api, props.appInfo))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setExact(true)
              .setPath("/guia")
              .setRender { route =>
                div(
                  menu(route.location.pathname),
                  TutorialComponent.component(TutorialComponent.Props(props.api, props.appInfo))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setExact(true)
              .setPath("/advanced")
              .setRender { route =>
                div(
                  menu(route.location.pathname),
                  AdvancedComponent.component(AdvancedComponent.Props(props.api))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setRender { _ =>
                router.Redirect("/")
              }
          )
        )
      )
    )
  }

  private def menu(path: String) = {
    mui.List(
      mui.ListSubheader.inset(true)(""),
      router.Link[String](to = "/")(
        mui.ListItem
          .button(true)
          .selected(path == "/")(mui.ListItemIcon(muiIcons.Home()), mui.ListItemText.primary("App"))
      ),
      router.Link[String](to = "/buenfin")(
        mui.ListItem
          .button(true)
          .selected(path == "/buenfin")(mui.ListItemIcon(muiIcons.Home()), mui.ListItemText.primary("El buen fin 2020"))
      ),
      router.Link[String](to = "/guia")(
        mui.ListItem
          .button(true)
          .selected(path == "/guia")(mui.ListItemIcon(muiIcons.Help()), mui.ListItemText.primary("Ayuda"))
      )
    )
  }
}
