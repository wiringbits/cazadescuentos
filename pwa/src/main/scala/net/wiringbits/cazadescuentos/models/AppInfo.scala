package net.wiringbits.cazadescuentos.models

import org.scalajs.dom.experimental.URLSearchParams
import typings.detectBrowser.{mod => DetectBrowser}

case class AppInfo(
    sharedUrl: Option[String],
    isAndroidApp: Boolean,
    browser: Option[DetectBrowser.BrowserInfo]
)

object AppInfo {

  def apply(query: URLSearchParams): AppInfo = {
    val browser = DetectBrowser.detect() match {
      case info: DetectBrowser.BrowserInfo => Some(info)
      case _ => None
    }

    val sharedUrl = Option(query.get("add-from-url")).map(_.trim).filter(_.nonEmpty)
    val isAndroidApp = Option(query.get("utm_source")).map(_.trim).contains("trusted-web-activity")
    new AppInfo(sharedUrl = sharedUrl, isAndroidApp = isAndroidApp, browser = browser)
  }
}
