package net.cazadescuentos.background

import java.util.UUID

import net.cazadescuentos.background.models.{Command, Event}
import net.cazadescuentos.background.services.ProductUpdaterService
import net.cazadescuentos.background.services.browser.BrowserNotificationService
import net.cazadescuentos.background.services.http.ProductHttpService
import net.cazadescuentos.background.services.storage.StorageService

import scala.concurrent.{ExecutionContext, Future}

private[background] class CommandProcessor(
    storageService: StorageService,
    productHttpService: ProductHttpService,
    browserNotificationService: BrowserNotificationService,
    productUpdaterService: ProductUpdaterService
)(implicit ec: ExecutionContext) {

  def process(buyerId: UUID, command: Command): Future[Event] = command match {
    case Command.FindBuyerId() =>
      for {
        maybe <- storageService.load()
        data = maybe.getOrElse(throw new RuntimeException("Data not ready, no buyer id found"))
      } yield Event.BuyerIdFound(data.buyerId)

    case Command.TrackProduct(product) =>
      for {
        details <- productHttpService.create(buyerId, product)
        _ <- storageService.add(details)
      } yield Event.ProductTracked(product)

    case Command.FindStoredProduct(id) =>
      storageService
        .find(id)
        .map(Event.FoundStoredProduct.apply)

    case Command.DeleteProduct(id) =>
      for {
        _ <- productHttpService.delete(buyerId, id)
        _ <- storageService.delete(id)
      } yield Event.StoredProductDeleted(id)

    case Command.GetStoredProductsSummary() =>
      for {
        remoteProducts <- productHttpService.getAllSummary(buyerId)
        _ <- productUpdaterService.updateThemAll(remoteProducts)
        data <- storageService.load().map(_.map(_.products).getOrElse(List.empty))
      } yield Event.GotStoredProducts(data)

    case Command.SendBrowserNotification(title, message) =>
      browserNotificationService.notify(title, message)
      Future.successful(Event.BrowserNotificationSent())
  }
}
