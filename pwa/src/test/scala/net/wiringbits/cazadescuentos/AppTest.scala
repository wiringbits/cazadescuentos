package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import net.wiringbits.cazadescuentos.api.http.models.{
  GetTrackedProductsResponse,
  NotificationsSubscription,
  ProductDetails
}
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.common.storage.StorageService
import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalajs.dom.document
import org.scalatest.funsuite.AnyFunSuite
import slinky.web.ReactDOM

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

      override def getAllSummaryV2(): Future[GetTrackedProductsResponse] = ???

      override def subscribe(subscription: NotificationsSubscription): Future[Unit] = Future.unit
    }

    val apis = API(productHttpService, storageService)

    val div = document.createElement("div")
    val appInfo = AppInfo(None, false, None)
    ReactDOM.render(App.component(App.Props(apis, appInfo)), div)
    ReactDOM.unmountComponentAtNode(div)
  }
}
