package net.wiringbits.cazadescuentos.common.storage

import io.circe.parser.parse
import io.circe.syntax._
import net.wiringbits.cazadescuentos.common.models.{ProductDetails, StoreProduct}
import net.wiringbits.cazadescuentos.common.storage.models.{StoredData, StoredProduct}
import org.scalajs.dom

class StorageService {

  import StorageService._

  def add(product: ProductDetails): Unit = {
    val candidate = StoredProduct(
      product.store,
      price = product.price,
      name = product.name,
      productId = product.productId,
      originalPrice = product.price,
      currencySymbol = product.currencySymbol,
      status = StoredProduct.Status.Available
    )
    val data = load().getOrElse(throw new RuntimeException("Missing setup, can't store products"))

    val existing = data.products
    val newAll = candidate :: existing
    val newData = data.copy(products = newAll)
    unsafeSet(newData)
  }

  def find(id: StoreProduct): Option[StoredProduct] = {
    load().flatMap(_.products.find(_.storeProduct == id))
  }

  def delete(id: StoreProduct): Unit = {
    val data = load().getOrElse(throw new RuntimeException("Missing data, can't delete product"))
    val products = data.products
    val newProducts = products.filter(_.storeProduct != id)
    val newData = data.copy(products = newProducts)
    unsafeSet(newData)
  }

  def batchUpdate(updatedData: List[StoredProduct]): Unit = {
    if (updatedData.isEmpty) {
      ()
    } else {
      load().foreach {
        case StoredData(buyerId, products) =>
          val newProducts = products.map { c =>
            updatedData.find(_.storeProduct == c.storeProduct).getOrElse(c)
          }
          unsafeSet(StoredData(buyerId, newProducts))
      }
    }
  }

  def load(): Option[StoredData] = {
    val json = dom.window.localStorage.getItem(Key)

    parse(json).toOption
      .flatMap(_.as[StoredData].toOption)
  }

  def unsafeSet(data: StoredData): Unit = {
    val json = data.asJson.noSpaces

    dom.window.localStorage.setItem(Key, json)
  }
}

object StorageService {

  private val Key = "data"
}
