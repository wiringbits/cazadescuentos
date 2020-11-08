package net.cazadescuentos

import net.cazadescuentos.activetab.ActiveTabConfig
import net.cazadescuentos.background.alarms.ProductUpdaterAlarm
import net.cazadescuentos.background.services.http.ProductHttpService

case class Config(
    serverUrl: String,
    httpConfig: ProductHttpService.Config,
    productUpdaterConfig: ProductUpdaterAlarm.Config,
    activeTabConfig: activetab.ActiveTabConfig
)

object Config {

  private val MaxProductsToFollow = 15
  private val DefaultServerUrl = "https://cazadescuentos.net/api"
  private val DevServerUrl = "http://localhost:9000"

  val Default: Config = {
    Config(
      serverUrl = DefaultServerUrl,
      ProductHttpService.Config(serverUrl = DefaultServerUrl),
      ProductUpdaterAlarm.Config(periodInMinutes = 60 * 3),
      ActiveTabConfig(maxProductsToFollow = MaxProductsToFollow)
    )
  }

  val Dev: Config = {
    Config(
      serverUrl = DevServerUrl,
      ProductHttpService.Config(serverUrl = DevServerUrl),
      ProductUpdaterAlarm.Config(periodInMinutes = 2),
      ActiveTabConfig(maxProductsToFollow = MaxProductsToFollow)
    )
  }
}
