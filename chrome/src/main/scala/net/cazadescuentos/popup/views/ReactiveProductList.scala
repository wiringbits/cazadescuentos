package net.cazadescuentos.popup.views

import com.thoughtworks.binding.Binding.Vars
import net.cazadescuentos.background.BackgroundAPI
import net.wiringbits.cazadescuentos.api.storage.models.StoredProduct

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ReactiveProductList(backgroundAPI: BackgroundAPI)(
    implicit ec: ExecutionContext
) {

  val data: Vars[StoredProduct] = Vars.empty

  def reload(): Unit = {
    backgroundAPI.getStoredProductsSummary().onComplete {
      case Success(products) =>
        log(s"Got products: ${products.size}")
        data.value.clear()
        data.value ++= products

      case Failure(ex) => log(s"Failed to get products: ${ex.getMessage}")
    }
  }

  def delete(storedProduct: StoredProduct): Unit = {
    data.value -= storedProduct
    backgroundAPI.deleteStoredProduct(storedProduct.storeProduct).onComplete {
      case Failure(ex) =>
        log(
          s"Failed to delete product ${storedProduct.storeProduct}: ${ex.getMessage}"
        )
      case Success(_) => ()
    }
  }

  private def log(msg: String): Unit = {
    println(s"ReactiveProductList: $msg")
  }
}
