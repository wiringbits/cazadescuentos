package net.cazadescuentos.background

import java.util.UUID

import io.circe.syntax._
import net.cazadescuentos.background.models.{Command, Event}
import net.cazadescuentos.models.{StoreProduct, StoredProduct}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.util.{Failure, Success, Try}

class BackgroundAPI(implicit ec: ExecutionContext) {

  import BackgroundAPI._

  def findBuyerId(): Future[UUID] = {
    val command: Command = Command.FindBuyerId()
    process(command).collect {
      case Event.BuyerIdFound(buyerId) => buyerId
    }
  }

  def trackProduct(product: StoreProduct): Future[Unit] = {
    val command: Command = Command.TrackProduct(product)
    process(command).collect {
      case _: Event.ProductTracked => ()
    }
  }

  def findStoredProduct(id: StoreProduct): Future[Option[StoredProduct]] = {
    val command: Command = Command.FindStoredProduct(id)
    process(command).collect {
      case e: Event.FoundStoredProduct => e.result
    }
  }

  def sendBrowserNotification(title: String, message: String): Future[Unit] = {
    val command: Command = Command.SendBrowserNotification(title, message)
    process(command).collect {
      case _: Event.BrowserNotificationSent => ()
    }
  }

  def deleteStoredProduct(id: StoreProduct): Future[Unit] = {
    val command: Command = Command.DeleteProduct(id)
    process(command).collect {
      case _: Event.StoredProductDeleted => ()
    }
  }

  def getStoredProductsSummary(): Future[List[StoredProduct]] = {
    val command: Command = Command.GetStoredProductsSummary()
    process(command).collect {
      case Event.GotStoredProducts(products) => products
    }
  }

  /**
   * Processes a command sending a message to the background context, when the background
   * isn't ready, the command is retried up to 3 times, delaying 1 second each time, this
   * retry strategy should be enough for most cases.
   */
  private def process(command: Command): Future[Event] = {
    val timeoutMs = 1000
    def processWithRetries(retriesLeft: Int, lastError: String): Future[Event] = {
      if (retriesLeft <= 0) {
        Future.successful(Event.CommandRejected(lastError))
      } else {
        val promise = Promise[Event]
        val _ = org.scalajs.dom.window.setTimeout(() => promise.completeWith(processInternal(command)), timeoutMs)

        promise.future
          .recoverWith {
            case TransientError(e) =>
              log(s"Trying to recover from transient error, retry = $retriesLeft, command = $command, error = $e")
              processWithRetries(retriesLeft - 1, e)
          }
      }
    }

    processInternal(command).recoverWith {
      case TransientError(e) =>
        log(s"Trying to recover from transient error, command = $command, error = $e")
        processWithRetries(3, e)
    }
  }

  private def processInternal(command: Command): Future[Event] = {
    val promise = Promise[Event]
    val callback: js.Function1[js.Object, Unit] = (x: js.Object) => {
      // On exceptional cases, the receiver isn't ready, leading to undefined object on the callback
      // One way this happens is when the extension browser-action is opened in a tab when the browser
      // starts, it tried to contact the background which isn't ready.
      //
      // On such cases, the lastError is supposed to include the failure reason but a user claims that
      // sometimes lastError is empty but the message is still undefined.
      //
      // We are handling both cases as a TransientError.
      //
      // This seems to occur only in Firefox, See:
      // - https://bugzilla.mozilla.org/show_bug.cgi?id=1435597
      // - https://discourse.mozilla.org/t/reply-to-chrome-runtime-sendmessage-is-undefined/25021/3
      chrome.runtime.Runtime.lastError
        .flatMap(Option.apply) // Apparently, chrome.runtime.Runtime.lastError could be Option(null)
        .flatMap(_.message.toOption)
        .orElse(
          Option.when(scalajs.js.isUndefined(x))("Got undefined message, receiver likely not ready")
        )
        .map { errorReason =>
          promise.failure(TransientError(errorReason))
        }
        .getOrElse {
          Try(x.asInstanceOf[String]).flatMap(Event.decode) match {
            case Success(Event.CommandRejected(reason)) =>
              sendBrowserNotification("ERROR", reason) // TODO: Remove hack
              promise.failure(new RuntimeException(reason))

            case Success(e: Event) =>
              promise.success(e)

            // Unable to parse the incoming message, it's likely the message wasn't sent by our app, no need to process it.
            case Failure(exception) => promise.failure(exception)
          }
        }
    }

    val message = command.asJson.noSpaces
    chrome.runtime.Runtime
      .sendMessage(message = message, responseCallback = callback)

    promise.future
  }

  private def log(msg: String): Unit = {
    println(s"BackgroundAPI: $msg")
  }
}

object BackgroundAPI {
  case class TransientError(message: String) extends RuntimeException
}
