package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.components.pages.{DiscountsPage, HelpPage, HomePage}
import net.wiringbits.cazadescuentos.components.AdvancedComponent
import net.wiringbits.cazadescuentos.components.widgets.AppMenu
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.{components => router}

@react object AppRouter {
  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    router.BrowserRouter.basename("")(
      AppMenu(),
      router.Switch(
        generateRoute("/", HomePage(props.api, props.appInfo)),
        generateRoute("/buenfin", DiscountsPage(props.api, props.appInfo)),
        generateRoute("/guia", HelpPage(props.api, props.appInfo)),
        generateRoute("/advanced", AdvancedComponent.component(AdvancedComponent.Props(props.api))),
        generateRoute("*", router.Redirect("/"), false)
      )
    )
  }

  private def generateRoute(path: String, component: ReactElement, exact: Boolean = true) = {
    router.Route(
      RouteProps()
        .setExact(exact)
        .setPath(path)
        .setChildren(component)
    )
  }
}
