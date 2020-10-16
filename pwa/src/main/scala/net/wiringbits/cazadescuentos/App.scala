package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.components._
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import typings.materialUiCore.{components => mui}
import typings.materialUiCore.createMuiThemeMod.{Theme, ThemeOptions}
import typings.materialUiCore.createTypographyMod.TypographyOptions
import typings.materialUiCore.stylesMod.createMuiTheme
import typings.materialUiCore.typographyTypographyMod.Style
import typings.materialUiIcons.{components => muiIcons}
import typings.materialUiStyles.components.ThemeProvider
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.{components => router}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

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
    val (discounts, setDiscounts) = Hooks.useState(List.empty[String])
    Hooks.useEffect(() => {
      props.api.productUpdaterService.updateThemAll().foreach { value =>
        setDiscounts(value)
      }
    }, "")
    ThemeProvider(theme)(
      router.BrowserRouter.basename("")(
        router.Route(
          RouteProps()
            .setExact(true)
            .setPath("/")
            .setRender(
              _ =>
                mui.List(
                  mui.ListSubheader.inset(true)(""),
//                    router.Link[String](to = "/select")(
//                      ListItem.button(true)(ListItemIcon(Icon.Assignment()), ListItemText.primary("Select"))
//                    ),
                  HomeComponent.component(HomeComponent.Props(props.api, props.appInfo, discounts))
                )
            )
        ),
        router.Route(
          RouteProps()
            .setPath("*")
            .setRender(_ => router.Redirect("/"))
        )
//          Route(
//            RouteProps()
//              .setPath("/select")
//              .setRender(
//                _ =>
//                  Fragment(
//                    Typography.variant(Style.h4).gutterBottom(true).component("h2")("Select"),
//                    SimpleTable(api)
//                  )
//              )
//          )
      )
    )
  }
}
