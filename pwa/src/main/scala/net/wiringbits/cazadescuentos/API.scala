package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import net.wiringbits.cazadescuentos.common.storage.StorageService

import scala.concurrent.ExecutionContext

case class API(
    productService: ProductHttpService,
    storageService: StorageService
)

object API {
//  private val serverApi = if (BuildInfo.production) {
//    "https://cazadescuentos.net/api"
//  } else {
//    "http://localhost:9000"
//  }

  private val serverApi = "https://cazadescuentos.net/api"

  def apply()(implicit ec: ExecutionContext): API = {
    println(s"Server API expected at: $serverApi")

    val storageService = new StorageService
    val productHttpServiceConfig = ProductHttpService.Config(serverApi)

    implicit val sttpBackend = sttp.client.FetchBackend()
    val productHttpService = new ProductHttpService.DefaultImpl(productHttpServiceConfig)

    API(productHttpService, storageService)
  }
}
