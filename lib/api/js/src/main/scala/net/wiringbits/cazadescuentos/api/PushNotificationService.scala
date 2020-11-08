package net.wiringbits.cazadescuentos.api

import java.util.Base64

import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import net.wiringbits.cazadescuentos.api.http.models.NotificationsSubscription
import org.scalajs.dom
import org.scalajs.dom.experimental.push._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.typedarray._

class PushNotificationService(config: PushNotificationService.Config, productHttpService: ProductHttpService) {

  def enableNotifications(): Future[Unit] = {
    for {
      registration <- dom.window.navigator.serviceWorker.ready.toFuture
      subscription <- getOrCreateSubscription(registration)
      _ = dom.console.log("Apparent subscription to be sent to the server", subscription)
      // Send the subscription details to the server
      _ <- productHttpService.subscribe(subscription)
    } yield ()
  }

  private def getOrCreateSubscription(
      registration: PushServiceWorkerRegistration
  ): Future[NotificationsSubscription] = {
    for {
      untypedSubscription <- registration.pushManager.getSubscription().toFuture
      subscription <- Option(untypedSubscription)
        .filterNot(js.isUndefined)
        .map(Future.successful)
        .getOrElse(requestPermissions(registration))
        .map(_.toJSON())
    } yield NotificationsSubscription(
      endpoint = subscription.endpoint,
      auth = subscription.keys(PushEncryptionKeyName.auth.toString),
      key = subscription.keys(PushEncryptionKeyName.p256dh.toString)
    )
  }

  private def requestPermissions(registration: PushServiceWorkerRegistration) = {
    registration.pushManager
      .subscribe(
        new PushSubscriptionOptions {
          userVisibleOnly = true
          applicationServerKey = config.vapidPublicKey
        }
      )
      .toFuture
  }
}

object PushNotificationService {
  private val DefaultVapidPublicKey =
    "BJBLz57nUySorHcTo-aHSAa0Mi86JF9Xs6rNViHLP3ZQNeWcxAhmcl4Ri0i4V2Tc_yBC3bLDeu1NbAButmzVD58"

  case class Config(vapidPublicKeyBae64Url: String) {

    def vapidPublicKey: Uint8Array = {
      val bytes = Base64.getUrlDecoder.decode(vapidPublicKeyBae64Url).toTypedArray
      new Uint8Array(bytes.buffer, bytes.byteOffset, bytes.length)
    }
  }

  def apply(productHttpService: ProductHttpService): PushNotificationService = {
    val config = Config(DefaultVapidPublicKey)
    new PushNotificationService(config, productHttpService)
  }
}
