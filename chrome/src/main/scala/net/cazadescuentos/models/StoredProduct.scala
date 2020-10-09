package net.cazadescuentos.models

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

/**
 * NOTE: This model represents what gets on the browser storage, so, be aware of the names to avoid
 * breaking compatibility.
 *
 * Think what'll happen when you deploy the new model and the storage has previous models, the new
 * one should be backwards compatible to avoid breaking the user stored items.
 */
case class StoredProduct(
    store: OnlineStore,
    name: String,
    price: BigDecimal,
    productId: String,
    originalPrice: BigDecimal,
    currencySymbol: String,
    status: StoredProduct.Status
) {

  def storeProduct: StoreProduct = StoreProduct(productId, store)

  def discountPercent: Int = {
    100 - (price * 100 / originalPrice).toInt
  }
  def sourceUrl: String = store.url(productId)
  def formattedPrice: String = s"$currencySymbol$price"
  def formattedOriginalPrice: String = s"$currencySymbol$originalPrice"
}

object StoredProduct {
  sealed abstract class Status(val string: String) extends Product with Serializable

  object Status {
    final case object Available extends Status("AVAILABLE")
    final case object Unavailable extends Status("UNAVAILABLE")

    def from(string: String): Option[Status] = string.toUpperCase match {
      case Status.Available.string => Some(Status.Available)
      case "NOT_AVAILABLE" | Status.Unavailable.string =>
        // "NOT_AVAILABLE" is used by the ts extension
        Some(Status.Unavailable)
      case _ => None
    }

    implicit val statusEncoder: Encoder[Status] =
      Encoder.instance(_.string.asJson)

    implicit val statusDecoder: Decoder[Status] = Decoder.decodeString.map { string =>
      from(string)
        .getOrElse(
          throw new RuntimeException(s"Unable to decode status = $string")
        )
    }
  }

  implicit val encoder: Encoder[StoredProduct] = deriveEncoder[StoredProduct]
  implicit val decoder: Decoder[StoredProduct] = deriveDecoder[StoredProduct]
}
