package net.cazadescuentos.popup

import net.cazadescuentos.background.BackgroundAPI
import net.cazadescuentos.common.I18NMessages
import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import org.scalajs.dom
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Runner(serverUrl: String, messages: I18NMessages, backgroundAPI: BackgroundAPI) {

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
        val productHttpServiceConfig = ProductHttpService.Config(serverUrl = serverUrl, buyerId = buyerId)
        implicit val sttpBackend = sttp.client.FetchBackend()
        val productHttpService = new ProductHttpService.DefaultImpl(productHttpServiceConfig)
        val api = API(backgroundAPI, productHttpService, messages)
        val app = App.component(App.Props(api))
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

  def apply(serverUrl: String)(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    new Runner(serverUrl, messages, backgroundAPI)
  }
}
