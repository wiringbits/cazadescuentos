package net.wiringbits.cazadescuentos

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.{Theme, ThemeOptions}
import com.alexitc.materialui.facade.materialUiCore.createTypographyMod.TypographyOptions
import com.alexitc.materialui.facade.materialUiCore.stylesMod.createMuiTheme
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import net.wiringbits.cazadescuentos.components._
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html.div
import typings.reactRouter.mod.{RouteComponentProps, RouteProps}
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
          // NOTE: For some reason the compiler started complaining while invoking the setPath method
          // fortunately, the setPathVarargs solved it, also, the setRender function started requiring
          // a explicit type.
          router.Route(
            RouteProps()
              .setExact(true)
              .setPathVarargs("/")
              .setRender { route: RouteComponentProps[_, _, _] =>
                div(
                  menu(route.location.pathname),
                  HomeComponent.component(HomeComponent.Props(props.api, props.appInfo))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setExact(true)
              .setPathVarargs("/buenfin")
              .setRender { route: RouteComponentProps[_, _, _] =>
                div(
                  menu(route.location.pathname),
                  DiscountsComponent.component(DiscountsComponent.Props(props.api, props.appInfo))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setExact(true)
              .setPathVarargs("/guia")
              .setRender { route: RouteComponentProps[_, _, _] =>
                div(
                  menu(route.location.pathname),
                  TutorialComponent.component(TutorialComponent.Props(props.api, props.appInfo))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setExact(true)
              .setPathVarargs("/advanced")
              .setRender { route: RouteComponentProps[_, _, _] =>
                div(
                  menu(route.location.pathname),
                  AdvancedComponent.component(AdvancedComponent.Props(props.api))
                )
              }
          ),
          router.Route(
            RouteProps()
              .setRender { _: RouteComponentProps[_, _, _] =>
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
