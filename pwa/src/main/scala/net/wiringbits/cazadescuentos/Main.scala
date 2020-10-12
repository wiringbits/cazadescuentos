package net.wiringbits.cazadescuentos

import org.scalajs.dom
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

    val apis = API()
    val pushNotificationService = PushNotificationService(apis.productService)
    ReactDOM.render(App.component(App.Props(apis)), container())
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
