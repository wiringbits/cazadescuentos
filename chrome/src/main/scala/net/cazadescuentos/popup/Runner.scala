package net.cazadescuentos.popup

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding._
import net.cazadescuentos.background.BackgroundAPI
import net.cazadescuentos.common.{I18NMessages, ResourceProvider}
import net.cazadescuentos.facades.CommonsFacade
import net.cazadescuentos.models.{OnlineStore, StoredProduct}
import net.cazadescuentos.popup.views.ReactiveProductList
import org.lrng.binding.html
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom

import scala.concurrent.ExecutionContext

class Runner(messages: I18NMessages, productList: ReactiveProductList) {

  private val isMobileDevice = CommonsFacade.isPopUpMobileResolution()

  def run(): Unit = {
    dom.document.onreadystatechange = _ => {
      if (dom.document.readyState == "interactive") {
        inject()
      }
    }
  }

  def inject(): Unit = {
    log("This was run by the popup script")

    val content = mainView(productList.data)
    html.render(dom.document.body, content)

    // load the data after painting the empty view to get a faster rendering
    productList.reload()
  }

  def mainView(products: Vars[StoredProduct]): Binding[dom.html.Div] = {
    Binding {
      if (products.isEmpty.bind) {
        emptyDroplistView.bind
      } else {
        nonEmptyDroplistView(products).bind
      }
    }
  }

  @html
  def nonEmptyDroplistView(products: Vars[StoredProduct]): NodeBinding[dom.html.Div] = {
    <div>
      <table class={getClassForTable()}>
        <thead class="thead-dark">
          <tr>
            <th>{messages.labelStore}</th>
            <th>{messages.labelProduct}</th>
            <th>{messages.labelPrice}</th>
            <th>{messages.labelDiscount}</th>
            <th>{messages.labelAvailable}</th>
            <th>{messages.labelInitialPrice}</th>
            <th>{messages.labelActions}</th>
          </tr>
        </thead>
        <tbody>
          {
      products.map(p => productRowView(p).bind)
    }
        </tbody>
      </table>
    </div>
  }

  @html
  def productRowView(product: StoredProduct): NodeBinding[dom.html.TableRow] = {
    <tr class={getClassForRow(product)}>
      <td data:data-title={messages.labelStore}>
        <img class="table-store-logo" src={getStoreImagePath(product.store)} alt={product.store.name}/>
      </td>
      <td data:data-title={messages.labelProduct}>{product.name}</td>
      <td data:data-title={messages.labelPrice}>{product.formattedPrice}</td>
      <td data:data-title={messages.labelDiscount}>{getContentForCell(product).bind}</td>
      <td data:data-title={messages.labelAvailable} data:data-toggle="tooltip" data:data-placement="top"
          data:title={product.status.string}>{getContentForStatusCell(product).bind}</td>
      <td data:data-title={messages.labelInitialPrice}>{product.formattedOriginalPrice}</td>
      <td class="icons-table" data:data-title={messages.labelActions}>
        <div onclick={_: dom.Event => productList.delete(product)}>
          <a>
            <i class="fas fa-trash-alt delete-icon"></i>
          </a>
        </div>
        <div>
          <a target="_blank" href={product.sourceUrl}>
            <i class="fas fa-link link-icon"></i>
          </a>
        </div>
      </td>
    </tr>
  }

  def getClassForRow(product: StoredProduct): String = {
    if (product.status == StoredProduct.Status.Unavailable) {
      "table-tr-dark"
    } else {
      ""
    }
  }

  @html
  def emptyDroplistView: NodeBinding[dom.html.Div] = {
    <div class="card" style="width: 18rem; display: block;">
      <div class="card-header text-center">
        <h5>{messages.appName}</h5>
      </div>
      <div class="card-body text-center">
        <div class="text-center">
          <img src={ResourceProvider.appIcon96} class="card-img-top" alt="..." style="max-width: 96px;"></img>
        </div>
        <h5 class="card-title">{messages.dropListEmpty}</h5>
        <p class="card-text">{messages.dropListEmptyDetailedMessage}</p>
      </div>
    </div>
  }

  @html
  def getContentForCell(product: StoredProduct): Binding[dom.html.Div] = {
    if (product.price < product.originalPrice) {
      <div><i class="fas fa-thumbs-up fa-flip-horizontal icon-thumbs-up "></i><span
      class="td-success">{product.discountPercent.toString} %</span></div>
    } else if (product.price > product.originalPrice) {
      <div><i class="fas fa-thumbs-down fa-flip-horizontal icon-thumbs-down"></i><span
      class="td-danger">{product.discountPercent.toString} %</span></div>
    } else
      <div><span>{
        product.discountPercent.toString
      }%</span></div>
  }

  @html
  def getContentForStatusCell(product: StoredProduct): Binding[dom.html.Element] = {
    if (product.status == StoredProduct.Status.Available) {
      <span data:data-tooltip={product.status.toString} data:data-tooltip-position="top" class="container-icon-status">
        <i class="far fa-check-circle icon-check-circle"></i>
      </span>
    } else {
      <span data:data-tooltip={product.status.toString} data:data-tooltip-position="top" class="container-icon-status">
        <i class="fas fa-ban icon-ban"></i>
      </span>
    }
  }

  def getClassForTable(): String = {
    if (isMobileDevice)
      "table-width table-hover table table-responsive"
    else "table-width table-hover table"
  }

  private def getStoreImagePath(store: OnlineStore): String = {
    s"/img/storeLogo/${store.storeLogo}"
  }

  private def log(msg: String): Unit = {
    println(s"popup: $msg")
  }
}

object Runner {

  def apply()(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    val productList = new ReactiveProductList(backgroundAPI)
    new Runner(messages, productList)
  }
}
