package net.cazadescuentos.background

import java.util.UUID

import net.cazadescuentos.background.models.{Command, Event}
import net.cazadescuentos.background.services.browser.BrowserNotificationService
import net.cazadescuentos.background.services.storage.StorageService
import net.wiringbits.cazadescuentos.api.http.ProductHttpService

import scala.concurrent.Future
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

private[background] class CommandProcessor(
    storageService: StorageService,
    productHttpService: ProductHttpService,
    browserNotificationService: BrowserNotificationService
) {

  def process(buyerId: UUID, command: Command): Future[Event] = command match {
    case Command.FindBuyerId() =>
      for {
        maybe <- storageService.load()
        data = maybe.getOrElse(throw new RuntimeException("Data not ready, no buyer id found"))
      } yield Event.BuyerIdFound(data.buyerId)

    case Command.TrackProduct(product) =>
      for {
        details <- productHttpService.create(buyerId, product)
      } yield Event.ProductTracked(details)

    case Command.FindStoredProduct(id) =>
      // TODO: Use API to check for specific item instead
      productHttpService
        .getAll(buyerId)
        .map(_.find(_.storeProduct == id))
        .map(Event.FoundStoredProduct.apply)

    case Command.DeleteProduct(id) =>
      for {
        _ <- productHttpService.delete(buyerId, id)
      } yield Event.StoredProductDeleted(id)

    case Command.GetStoredProductsSummary() =>
      for {
        products <- productHttpService.getAllSummary(buyerId)
      } yield Event.GotStoredProducts(products)

    case Command.SendBrowserNotification(title, message) =>
      browserNotificationService.notify(title, message)
      Future.successful(Event.BrowserNotificationSent())
  }
}
