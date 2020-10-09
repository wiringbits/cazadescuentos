package net.cazadescuentos

import net.cazadescuentos.activetab.ActiveTabConfig
import net.cazadescuentos.background.alarms.ProductUpdaterAlarm
import net.cazadescuentos.background.services.http.ProductHttpService

case class Config(
    httpConfig: ProductHttpService.Config,
    productUpdaterConfig: ProductUpdaterAlarm.Config,
    activeTabConfig: activetab.ActiveTabConfig
)

object Config {

  private val MaxProductsToFollow = 15

  val Default: Config = {
    Config(
      ProductHttpService.Config(serverUrl = "https://cazadescuentos.net/api"),
      ProductUpdaterAlarm.Config(periodInMinutes = 60 * 3),
      ActiveTabConfig(maxProductsToFollow = MaxProductsToFollow)
    )
  }

  val Dev: Config = {
    Config(
      ProductHttpService.Config(serverUrl = "http://localhost:9000"),
      ProductUpdaterAlarm.Config(periodInMinutes = 2),
      ActiveTabConfig(maxProductsToFollow = MaxProductsToFollow)
    )
  }
}
