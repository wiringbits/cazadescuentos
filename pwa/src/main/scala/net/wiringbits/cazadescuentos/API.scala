package net.wiringbits.cazadescuentos

import java.util.UUID

import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import net.wiringbits.cazadescuentos.api.storage.models.StoredData
import net.wiringbits.cazadescuentos.common.services.ProductUpdaterService
import net.wiringbits.cazadescuentos.common.storage.StorageService

import scala.concurrent.ExecutionContext

case class API(
    productService: ProductHttpService,
    storageService: StorageService,
    productUpdaterService: ProductUpdaterService
)

object API {
  private val serverApi = if (BuildInfo.production) {
    "https://cazadescuentos.net/api"
  } else {
    "http://localhost:9000"
  }

  def apply()(implicit ec: ExecutionContext): API = {
    println(s"Server API expected at: $serverApi")

    val storageService = new StorageService
    val buyerId = findBuyerId(storageService)
    val productHttpServiceConfig = ProductHttpService.Config(serverApi, buyerId)

    implicit val sttpBackend = sttp.client.FetchBackend()
    val productHttpService = new ProductHttpService.DefaultImpl(productHttpServiceConfig)

    val productUpdaterService = new ProductUpdaterService(productHttpService, storageService)
    API(productHttpService, storageService, productUpdaterService)
  }

  private def findBuyerId(storageService: StorageService): UUID = {
    storageService.load() match {
      case Some(value) => value.buyerId
      case None =>
        val newBuyerId = UUID.randomUUID()
        val storedData = StoredData(newBuyerId, List.empty)
        storageService.unsafeSet(storedData)
        newBuyerId
    }
  }
}
