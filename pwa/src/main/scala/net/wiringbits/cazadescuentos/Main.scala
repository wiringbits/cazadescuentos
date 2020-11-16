package net.wiringbits.cazadescuentos

import java.util.UUID

import net.wiringbits.cazadescuentos.api.PushNotificationService
import net.wiringbits.cazadescuentos.api.storage.models.StoredData
import net.wiringbits.cazadescuentos.common.storage.StorageService
import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalajs.dom
import org.scalajs.dom.experimental.URLSearchParams
import org.scalajs.dom.experimental.serviceworkers._
import slinky.hot
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.LinkingInfo
import scala.util.{Failure, Success}

object Main {

  def main(argv: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val apis = API()
    val buyerId = findBuyerId(apis.storageService)
    val appInfo = AppInfo(buyerId, new URLSearchParams(dom.window.location.search))
    val app = appInfo.sharedUrl match {
      case Some(_) =>
        SharedItemApp.component(SharedItemApp.Props(apis, appInfo))

      case None =>
        App.component(App.Props(apis, appInfo))
    }

    ReactDOM.render(app, container())
    registerServiceWorker().onComplete {
      case Success(_) =>
        println("Service worker registered, trying to enable push notifications")
        val pushNotificationService = PushNotificationService(apis.productService)
        pushNotificationService
          .enableNotifications(buyerId)
          .onComplete {
            case Success(_) => println("Push notifications enabled")
            case Failure(ex) => println(s"Failed to enable push notifications: ${ex.getMessage}")
          }

      case Failure(ex) => println(s"Failed to register service worker, push notifications disabled: ${ex.getMessage}")
    }
  }

  private def container(): dom.Element = {
    Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }
  }

  private def registerServiceWorker(): Future[Unit] = {
    val promise = Promise[Unit]()
    dom.window.addEventListener("load", (_: dom.Event) => {
      val f = dom.window.navigator.serviceWorker
        .register("/service-worker.js")
        .toFuture
        .map(_ => ())

      promise.completeWith(f)
    })

    promise.future
  }

  private def findBuyerId(storageService: StorageService): UUID = {
    storageService.load() match {
      case Some(value) => value.buyerId
      case None =>
        val newBuyerId = UUID.randomUUID()
        val storedData = StoredData(newBuyerId)
        storageService.unsafeSet(storedData)
        newBuyerId
    }
  }
}
