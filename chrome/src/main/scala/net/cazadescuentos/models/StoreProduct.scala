package net.cazadescuentos.models

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class StoreProduct(id: String, store: OnlineStore) {
  val url: String = store.baseUrl + id
}

object StoreProduct {

  implicit val encoder: Encoder[StoreProduct] = deriveEncoder[StoreProduct]
  implicit val decoder: Decoder[StoreProduct] = deriveDecoder[StoreProduct]
}
