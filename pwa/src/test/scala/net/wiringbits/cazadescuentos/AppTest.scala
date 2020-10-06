package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.common.http.ProductHttpService
import net.wiringbits.cazadescuentos.common.models.{ProductDetails, StoreProduct}
import net.wiringbits.cazadescuentos.common.services.ProductUpdaterService
import net.wiringbits.cazadescuentos.common.storage.StorageService
import org.scalajs.dom.document
import org.scalatest.funsuite.AnyFunSuite
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AppTest extends AnyFunSuite {
  test("Renders without crashing") {
    val storageService = new StorageService
    val productHttpService = new ProductHttpService {
      override def create(storeProduct: StoreProduct): Future[ProductDetails] = {
        Future.failed(new Exception("Unable to create product"))
      }

      override def delete(storeProduct: StoreProduct): Future[Unit] = Future.unit

      override def getAll(): Future[List[ProductDetails]] = Future.successful(List.empty)

      override def getAllSummary(): Future[List[ProductDetails]] = Future.successful(List.empty)
    }

    val productUpdaterService = new ProductUpdaterService(productHttpService, storageService)
    val apis = API(productHttpService, storageService, productUpdaterService)

    val div = document.createElement("div")
    ReactDOM.render(App.component(App.Props(apis)), div)
    ReactDOM.unmountComponentAtNode(div)
  }
}
