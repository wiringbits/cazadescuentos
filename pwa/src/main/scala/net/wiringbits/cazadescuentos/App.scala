package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.providers.InstallPromptProvider
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object App {
  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    AppTheme(
      InstallPromptProvider(
        AppRouter(props.api, props.appInfo)
      )
    )
  }
}
