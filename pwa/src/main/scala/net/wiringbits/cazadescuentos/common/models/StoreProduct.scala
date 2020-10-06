package net.wiringbits.cazadescuentos.common.models

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class StoreProduct(id: String, store: OnlineStore) {
  val url: String = store.baseUrl + id
}

object StoreProduct {

  implicit val encoder: Encoder[StoreProduct] = deriveEncoder[StoreProduct]
  implicit val decoder: Decoder[StoreProduct] = deriveDecoder[StoreProduct]

  def parse(url: String): Option[StoreProduct] = {
    OnlineStore.available
      .collectFirst {
        case store if url.startsWith(store.baseUrl) && url.length > store.baseUrl.length =>
          store -> url.drop(store.baseUrl.length)
      }
      .filter { case (store, productId) => store.looksLikeProduct(productId) }
      .map {
        case (store, productId) =>
          StoreProduct(productId, store)
      }
  }
}
