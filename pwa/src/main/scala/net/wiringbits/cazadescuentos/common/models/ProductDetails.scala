package net.wiringbits.cazadescuentos.common.models

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
 * NOTE: This model represents what the http server returns, so, be aware of the names to avoid
 * breaking compatibility.
 */
case class ProductDetails(
    store: OnlineStore,
    productId: String,
    price: BigDecimal,
    currencySymbol: String,
    name: String
) {

  def storeProduct: StoreProduct = StoreProduct(productId, store)
}

object ProductDetails {

  implicit val encoder: Encoder[ProductDetails] = deriveEncoder[ProductDetails]
  implicit val decoder: Decoder[ProductDetails] = deriveDecoder[ProductDetails]

}
