package net.wiringbits.cazadescuentos

import java.util.UUID

import net.wiringbits.cazadescuentos.common.http.ProductHttpService
import net.wiringbits.cazadescuentos.common.services.ProductUpdaterService
import net.wiringbits.cazadescuentos.common.storage.StorageService
import net.wiringbits.cazadescuentos.common.storage.models.StoredData
import org.scalajs.dom
import slinky.hot
import slinky.web.ReactDOM

import scala.scalajs.LinkingInfo

object Main {

  private val serverApi = if (BuildInfo.production) {
    "https://cazadescuentos.net/api"
  } else {
    "http://localhost:9000"
  }

  def main(argv: Array[String]): Unit = {
    println(s"Server API expected at:: $serverApi")
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    import scala.concurrent.ExecutionContext.Implicits.global

    val storageService = new StorageService
    val buyerId = storageService.load() match {
      case Some(value) => value.buyerId
      case None =>
        val newBuyerId = UUID.randomUUID()
        val storedData = StoredData(newBuyerId, List.empty)
        storageService.unsafeSet(storedData)
        newBuyerId
    }

    val productHttpServiceConfig = ProductHttpService.Config(serverApi, buyerId)
    val productHttpService = ProductHttpService(productHttpServiceConfig)
    val productUpdaterService = new ProductUpdaterService(productHttpService, storageService)
    val apis = API(productHttpService, storageService, productUpdaterService)

    ReactDOM.render(App.component(App.Props(apis)), container)
  }
}
