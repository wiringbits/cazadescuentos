package net.cazadescuentos.activetab

import net.cazadescuentos.Config
import net.cazadescuentos.background.BackgroundAPI
import net.cazadescuentos.common.I18NMessages
import net.cazadescuentos.facades.sidenavCzd
import net.wiringbits.cazadescuentos.common.models.{OnlineStore, StoreProduct}
import org.scalajs.dom

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.util.{Failure, Success}

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages)(
    implicit ec: ExecutionContext
) {

  private var lastUrl = ""

  private var mySidenav: Option[sidenavCzd.customSidenav] = None

  def run(): Unit = {
    log("This was run by the active tab")

    scheduleUrlCheck()
  }

  private def scheduleUrlCheck(): Unit = {
    dom.window.setTimeout(checkCurrentUrl, 2000)
  }

  private val checkCurrentUrl: js.Function0[Unit] = () => {
    val current = dom.window.location.href
    if (current != lastUrl) {
      lastUrl = current
      // The url is case-sensitive, we shouldn't lower/upper case it
      verifyProduct(current)
    }

    scheduleUrlCheck()
  }

  private def verifyProduct(url: String): Unit = {
    mySidenav.foreach(customSidenav => customSidenav.destroy())
    OnlineStore.available
      .collectFirst {
        case store if url.startsWith(store.baseUrl) && url.length > store.baseUrl.length =>
          store -> url.drop(store.baseUrl.length)
      }
      .filter { case (store, productId) => store.looksLikeProduct(productId) }
      .foreach {
        case (store, productId) =>
          log(s"Product found on $store")
          val product = StoreProduct(productId, store)
          backgroundAPI
            .findStoredProduct(product)
            .flatMap {
              case None =>
                log("Product not found, asking for permissions to track it")
                tryToTrackProduct(product)

              case Some(_) =>
                log("Product found, notifying")
                backgroundAPI
                  .sendBrowserNotification(
                    messages.appName,
                    messages.youAreFollowingThisItem
                  )
            }
            .onComplete {
              case Success(_) => println("done")
              case Failure(ex) =>
                log(s"failed to verify product, e = $ex")
            }
      }
  }

  private def tryToTrackProduct(product: StoreProduct): Future[Unit] = {
    confirmUserWillTrackProduct(product).flatMap { confirmed =>
      if (confirmed) {
        for {
          _ <- backgroundAPI.trackProduct(product)
          _ <- backgroundAPI.sendBrowserNotification(
            messages.appName,
            messages.youAreFollowingThisItem
          )
        } yield ()
      } else {
        Future.unit
      }
    }
  }

  private def confirmUserWillTrackProduct(
      product: StoreProduct
  ): Future[Boolean] = {
    mySidenav = Some(new sidenavCzd.customSidenav())
    mySidenav
      .map(
        mysidenav =>
          mysidenav.fire(new sidenavCzd.Options {
            title = messages.appName
            text = messages.questionBeforeFollowingItem
            imgLogo = chrome.runtime.Runtime.getURL("icons/96/app.png")
            btnAcceptTxt = messages.yes
            btnCancelTxt = messages.no
            draggable = true
          })
      )
      .get
      .toFuture
  }

  private def log(msg: String): Unit = {
    println(s"activeTab: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
