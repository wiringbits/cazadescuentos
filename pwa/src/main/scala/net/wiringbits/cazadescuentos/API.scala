package net.wiringbits.cazadescuentos

import java.util.UUID

import net.wiringbits.cazadescuentos.common.http.ProductHttpService
import net.wiringbits.cazadescuentos.common.services.ProductUpdaterService
import net.wiringbits.cazadescuentos.common.storage.StorageService
import net.wiringbits.cazadescuentos.common.storage.models.StoredData

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
    val productHttpService = ProductHttpService(productHttpServiceConfig)
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
