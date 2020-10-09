package net.cazadescuentos.background.services

import java.util.UUID

import net.cazadescuentos.background.services.browser.BrowserNotificationService
import net.cazadescuentos.background.services.http.ProductHttpService
import net.cazadescuentos.background.services.storage.StorageService
import net.cazadescuentos.common.I18NMessages
import net.cazadescuentos.models.{ProductDetails, StoredProduct}

import scala.concurrent.{ExecutionContext, Future}

class ProductUpdaterService(
    productHttpService: ProductHttpService,
    storageService: StorageService,
    notificationService: BrowserNotificationService,
    messages: I18NMessages
)(implicit ec: ExecutionContext) {

  // TODO: Return found discounts instead of notifying directly
  def updateThemAll(buyerId: UUID): Future[Unit] = {
    for {
      remoteProducts <- productHttpService.getAll(buyerId)
      _ <- updateThemAll(remoteProducts)
    } yield ()
  }

  // TODO: Return found discounts instead of notifying directly
  def updateThemAll(remoteProducts: List[ProductDetails]): Future[Unit] = {
    for {
      dataMaybe <- storageService.load()
      products = dataMaybe.map(_.products).getOrElse(List.empty)
      _ = log(s"Got local products = ${products.size}")

      newData = products.map { p =>
        val maybe = remoteProducts.find(_.storeProduct == p.storeProduct)
        p -> maybe
      }
      _ = log(s"Got products latest details")
      _ <- Future.sequence {
        newData.collect {
          case (old, Some(latest)) if hasPriceDecreased(old, latest) =>
            sendNotification(old, latest)
        }
      }
      updatedProducts = newData.flatMap { case (a, b) => update(a, b) }
      _ <- storageService.batchUpdate(updatedProducts)
      _ = log(s"products updated: ${updatedProducts.size}")
    } yield ()
  }

  private def hasPriceDecreased(old: StoredProduct, latest: ProductDetails): Boolean = {
    old.price != latest.price &&
    latest.price < old.originalPrice
  }

  private def sendNotification(old: StoredProduct, latest: ProductDetails): Future[Unit] = {
    Future {
      val newData = old.copy(price = latest.price)
      log(
        s"Discount found, store = ${old.store}, product = ${old.name}, percent = ${newData.discountPercent}"
      )

      val message = messages.productHasDiscountNotification(
        latest.name,
        latest.store,
        newData.discountPercent
      )
      notificationService.notify(message)
    }
  }

  // returns the new product details only if they have changed8
  private def update(old: StoredProduct, newDetailsMaybe: Option[ProductDetails]): Option[StoredProduct] = {
    newDetailsMaybe match {
      case None if old.status == StoredProduct.Status.Available =>
        // the product was available and we weren't able to retrieve the new details, mark it as unavailable
        val newProduct = old.copy(status = StoredProduct.Status.Unavailable)
        Some(newProduct)

      case Some(newDetails) if old.status == StoredProduct.Status.Unavailable =>
        // the product was unavailable and we were able to retrieve the details, mark it as available and set the current price
        val newProduct = old.copy(
          status = StoredProduct.Status.Available,
          price = newDetails.price
        )
        Some(newProduct)

      case Some(newDetails) if newDetails.price != old.price =>
        // the price has changed
        val newProduct = old.copy(price = newDetails.price)
        Some(newProduct)

      case None | Some(_) => None // no changes
    }
  }

  private def log(msg: String): Unit = {
    println(s"productUpdaterService: $msg")
  }
}
