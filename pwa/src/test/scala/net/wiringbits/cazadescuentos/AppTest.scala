package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import net.wiringbits.cazadescuentos.api.http.models.{
  GetDiscountsResponse,
  GetNotificationsResponse,
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
import java.util.UUID

class AppTest extends AnyFunSuite {
  val BUYER_ID: UUID = UUID.fromString("2145bd98-c968-11ec-9d64-0242ac120002")

  test("Renders without crashing") {
    val storageService = new StorageService

    val productHttpService = new ProductHttpService {
      override def bestDiscounts(buyerId: UUID): Future[GetDiscountsResponse] = {
        Future.successful(GetDiscountsResponse(List.empty))
      }

      override def create(buyerId: UUID, storeProduct: StoreProduct): Future[ProductDetails] = {
        Future.failed(new Exception("Unable to create product"))
      }

      override def delete(buyerId: UUID, storeProduct: StoreProduct): Future[Unit] = Future.unit

      override def getAll(buyerId: UUID): Future[List[ProductDetails]] = Future.successful(List.empty)

      override def getAllSummary(buyerId: UUID): Future[List[ProductDetails]] = Future.successful(List.empty)

      override def getAllSummaryV2(buyerId: UUID): Future[GetTrackedProductsResponse] = Future.successful(
        GetTrackedProductsResponse(List.empty)
      )

      override def subscribe(buyerId: UUID, subscription: NotificationsSubscription): Future[Unit] = Future.unit

      override def notificationRead(buyerId: UUID, id: UUID): Future[Unit] = Future.unit

      override def notifications(buyerId: UUID): Future[GetNotificationsResponse] = {
        Future.successful(GetNotificationsResponse(List.empty))
      }
    }

    val apis = API(productHttpService, storageService)
    val div = document.createElement("div")
    val appInfo = AppInfo(buyerId = BUYER_ID, sharedUrl = None, isAndroidApp = false, browser = None)

    ReactDOM.render(App.component(App.Props(apis, appInfo)), div)
    ReactDOM.unmountComponentAtNode(div)
  }
}
