package net.wiringbits.cazadescuentos.models

import java.util.UUID

import org.scalajs.dom.experimental.URLSearchParams
import typings.detectBrowser.{mod => DetectBrowser}

case class AppInfo(
    buyerId: UUID,
    sharedUrl: Option[String],
    isAndroidApp: Boolean,
    browser: Option[DetectBrowser.BrowserInfo]
)

object AppInfo {

  def apply(buyerId: UUID, query: URLSearchParams): AppInfo = {
    val browser = DetectBrowser.detect() match {
      case info: DetectBrowser.BrowserInfo => Some(info)
      case _ => None
    }

    val sharedUrl = Option(query.get("add-from-url")).map(_.trim).filter(_.nonEmpty)
    val isAndroidApp = Option(query.get("utm_source")).map(_.trim).contains("trusted-web-activity")
    new AppInfo(buyerId = buyerId, sharedUrl = sharedUrl, isAndroidApp = isAndroidApp, browser = browser)
  }
}
