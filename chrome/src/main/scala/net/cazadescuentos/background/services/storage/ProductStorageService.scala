package net.cazadescuentos.background.services.storage

import io.circe.parser.parse
import io.circe.syntax._
import net.wiringbits.cazadescuentos.api.codecs.CirceCodecs._
import net.wiringbits.cazadescuentos.api.http.models.ProductDetails
import net.wiringbits.cazadescuentos.api.storage.models.StoredProduct
import net.wiringbits.cazadescuentos.common.models.StoreProduct

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

private[background] class ProductStorageService(implicit ec: ExecutionContext) {

  import ProductStorageService._

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
      existing <- getAll()
      newAll = candidate :: existing
      _ <- unsafeSet(newAll)
    } yield ()
  }

  def find(id: StoreProduct): Future[Option[StoredProduct]] = {
    getAll().map { products =>
      products.find(_.storeProduct == id)
    }
  }

  def delete(id: StoreProduct): Future[Unit] = {
    for {
      products <- getAll()
      newProducts = products.filter(_.storeProduct != id)
      _ <- unsafeSet(newProducts)
    } yield ()
  }

  def batchUpdate(updatedData: List[StoredProduct]): Future[Unit] = {
    if (updatedData.isEmpty) {
      Future.unit
    } else {
      getAll().flatMap { products =>
        val newProducts = products.map { c =>
          updatedData.find(_.storeProduct == c.storeProduct).getOrElse(c)
        }
        unsafeSet(newProducts)
      }
    }
  }

  def getAll(): Future[List[StoredProduct]] = {
    chrome.storage.Storage.local
      .get(Key: js.Any)
      .map(_.asInstanceOf[js.Dictionary[String]])
      .map { dict =>
        val json = dict.getOrElse(Key, "[]")
        parse(json).toOption
          .flatMap(_.as[List[StoredProduct]].toOption)
          .getOrElse(List.empty)
      }
  }

  def clear(): Future[Unit] = {
    chrome.storage.Storage.local.remove(Key)
  }

  private def unsafeSet(products: List[StoredProduct]): Future[Unit] = {
    val json = products.asJson.noSpaces
    val dict = js.Dictionary(Key -> js.Any.fromString(json))

    chrome.storage.Storage.local.set(dict)
  }
}

private[background] object ProductStorageService {

  private val Key = "products"
}
