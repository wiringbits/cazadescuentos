package net.wiringbits.cazadescuentos.common.services

import net.wiringbits.cazadescuentos.common.http.ProductHttpService
import net.wiringbits.cazadescuentos.common.models.ProductDetails
import net.wiringbits.cazadescuentos.common.storage.StorageService
import net.wiringbits.cazadescuentos.common.storage.models.StoredProduct

import scala.concurrent.{ExecutionContext, Future}

class ProductUpdaterService(
    productHttpService: ProductHttpService,
    storageService: StorageService
)(implicit ec: ExecutionContext) {

  def updateThemAll(): Future[List[String]] = {
    for {
      remoteProducts <- productHttpService.getAll()
      notifications <- updateThemAll(remoteProducts)
    } yield notifications
  }

  // Returns the notification messages on the new discounts
  def updateThemAll(remoteProducts: List[ProductDetails]): Future[List[String]] = {
    for {
      dataMaybe <- Future {
        storageService.load()
      }
      products = dataMaybe.map(_.products).getOrElse(List.empty)
      _ = log(s"Got local products = ${products.size}")

      newData = products.map { p =>
        val maybe = remoteProducts.find(_.storeProduct == p.storeProduct)
        p -> maybe
      }
      _ = log(s"Got products latest details")
      notifications = newData.collect {
        case (old, Some(latest)) if hasPriceDecreased(old, latest) =>
          prepareNotification(old, latest)
      }
      updatedProducts = newData.flatMap { case (a, b) => update(a, b) }
      _ = storageService.batchUpdate(updatedProducts)
      _ = log(s"products updated: ${updatedProducts.size}")
    } yield notifications
  }

  private def hasPriceDecreased(old: StoredProduct, latest: ProductDetails): Boolean = {
    old.price != latest.price &&
    latest.price < old.originalPrice
  }

  private def prepareNotification(old: StoredProduct, latest: ProductDetails): String = {
    val newData = old.copy(price = latest.price)
    log(
      s"Discount found, store = ${old.store}, product = ${old.name}, percent = ${newData.discountPercent}"
    )

    val message = s"${latest.name} de ${latest.store} ha bajado ${newData.discountPercent}% de precio"
    message
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
