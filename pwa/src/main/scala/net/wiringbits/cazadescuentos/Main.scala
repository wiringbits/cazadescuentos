package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalajs.dom
import org.scalajs.dom.experimental.URLSearchParams
import org.scalajs.dom.experimental.serviceworkers._
import slinky.hot
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.LinkingInfo

object Main {

  def main(argv: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val appInfo = AppInfo(new URLSearchParams(dom.window.location.search))
    val apis = API()
    val app = appInfo.sharedUrl match {
      case Some(_) =>
        SharedItemApp.component(SharedItemApp.Props(apis, appInfo))

      case None =>
        App.component(App.Props(apis, appInfo))
    }

    val pushNotificationService = PushNotificationService(apis.productService)
    ReactDOM.render(app, container())
    registerServiceWorker()
    pushNotificationService.enableNotifications()
  }

  private def container(): dom.Element = {
    Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }
  }

  private def registerServiceWorker(): Unit = {
    dom.window.addEventListener("load", (_: dom.Event) => {
      dom.window.navigator.serviceWorker.register("/service-worker.js")
    })
  }
}
