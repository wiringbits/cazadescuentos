package net.cazadescuentos.background.models

import java.util.UUID

import io.circe.generic.auto._
import io.circe.parser.parse
import net.wiringbits.cazadescuentos.api.http.models.ProductDetails
import net.wiringbits.cazadescuentos.common.models.StoreProduct

import scala.util.Try

private[background] sealed trait Event extends Product with Serializable

private[background] object Event {

  final case class BuyerIdFound(buyerId: UUID) extends Event
  final case class ProductTracked(product: ProductDetails) extends Event
  final case class FoundStoredProduct(result: Option[ProductDetails]) extends Event
  final case class GotStoredProducts(products: List[ProductDetails]) extends Event
  final case class StoredProductDeleted(id: StoreProduct) extends Event
  final case class BrowserNotificationSent() extends Event
  final case class CommandRejected(reason: String) extends Event

  def decode(string: String): Try[Event] = {
    parse(string).toTry
      .flatMap(_.as[Event].toTry)
  }
}
