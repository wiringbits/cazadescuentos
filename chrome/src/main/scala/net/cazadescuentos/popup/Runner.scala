package net.cazadescuentos.popup

import net.cazadescuentos.Config
import net.cazadescuentos.background.BackgroundAPI
import net.cazadescuentos.common.I18NMessages
import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import org.scalajs.dom
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Runner(productHttpService: ProductHttpService, messages: I18NMessages, backgroundAPI: BackgroundAPI) {

  def run(): Unit = {
    dom.document.onreadystatechange = _ => {
      if (dom.document.readyState == "interactive") {
        inject()
      }
    }
  }

  def inject(): Unit = {
    log("This was run by the popup script")
    import scala.concurrent.ExecutionContext.Implicits.global
    backgroundAPI.findBuyerId().onComplete {
      case Success(buyerId) =>
        val api = API(backgroundAPI, productHttpService, messages)
        val appInfo = AppInfo(buyerId)
        val app = App.component(App.Props(api, appInfo))
        ReactDOM.render(app, container())

      case Failure(ex) =>
        log(s"Unable to find the buyer id, application data likely corrupted: ${ex.getMessage}")
        val content = dom.document.createElement("p")
        content.textContent = messages.applicationCorrupted
        dom.document.appendChild(content)
        container()
    }
  }

  private def container(): dom.Element = {
    Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }
  }

  private def log(msg: String): Unit = {
    println(s"popup: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()

    implicit val sttpBackend = sttp.client.FetchBackend()
    val http = new ProductHttpService.DefaultImpl(config.httpConfig)
    new Runner(http, messages, backgroundAPI)
  }
}
