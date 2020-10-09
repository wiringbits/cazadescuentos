package net.cazadescuentos.background.services.storage

import io.circe.parser.parse
import io.circe.syntax._
import net.cazadescuentos.background.services.storage.models.StoredData
import net.cazadescuentos.models.{ProductDetails, StoreProduct, StoredProduct}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

private[background] class StorageService(implicit ec: ExecutionContext) {

  import StorageService._

  def add(product: ProductDetails): Future[Unit] = {
    val candidate = StoredProduct(
      product.store,
      price = product.price,
      name = product.name,
      productId = product.productId,
      originalPrice = product.price,
      currencySymbol = product.currencySymbol,
      status = StoredProduct.Status.Available
    )

    for {
      dataMaybe <- load()
      data = dataMaybe.getOrElse(throw new RuntimeException("Missing setup, can't store products"))
      existing = data.products
      newAll = candidate :: existing
      newData = data.copy(products = newAll)
      _ <- unsafeSet(newData)
    } yield ()
  }

  def find(id: StoreProduct): Future[Option[StoredProduct]] = {
    load().map { maybe =>
      maybe.flatMap(_.products.find(_.storeProduct == id))
    }
  }

  def delete(id: StoreProduct): Future[Unit] = {
    for {
      dataMaybe <- load()
      data = dataMaybe.getOrElse(throw new RuntimeException("Missing data, can't delete product"))
      products = data.products
      newProducts = products.filter(_.storeProduct != id)
      newData = data.copy(products = newProducts)
      _ <- unsafeSet(newData)
    } yield ()
  }

  def batchUpdate(updatedData: List[StoredProduct]): Future[Unit] = {
    if (updatedData.isEmpty) {
      Future.unit
    } else {
      load().flatMap { maybe =>
        val data = maybe.getOrElse(throw new RuntimeException("Can't update, missing data"))
        val products = data.products
        val newProducts = products.map { c =>
          updatedData.find(_.storeProduct == c.storeProduct).getOrElse(c)
        }
        val newData = data.copy(products = newProducts)
        unsafeSet(newData)
      }
    }
  }

  def load(): Future[Option[StoredData]] = {
    chrome.storage.Storage.local
      .get(Key: js.Any)
      .map(_.asInstanceOf[js.Dictionary[String]])
      .map { dict =>
        val json = dict.getOrElse(Key, "{}")
        parse(json).toOption
          .flatMap(_.as[StoredData].toOption)
      }
  }

  def unsafeSet(data: StoredData): Future[Unit] = {
    val json = data.asJson.noSpaces
    val dict = js.Dictionary(Key -> js.Any.fromString(json))

    chrome.storage.Storage.local.set(dict)
  }
}

object StorageService {

  private val Key = "data"
}
