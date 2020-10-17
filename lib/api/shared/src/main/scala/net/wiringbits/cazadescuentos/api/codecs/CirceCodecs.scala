package net.wiringbits.cazadescuentos.api.codecs

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import net.wiringbits.cazadescuentos.api.http.models.ProductDetails
import net.wiringbits.cazadescuentos.api.storage.models.{StoredData, StoredProduct}
import net.wiringbits.cazadescuentos.common.models.{OnlineStore, StoreProduct}

object CirceCodecs {
  implicit val storeEncoder: Encoder[OnlineStore] = Encoder.instance { x: OnlineStore =>
    x.id.asJson
  }

  implicit val storeDecoder: Decoder[OnlineStore] = Decoder.decodeString.map { string =>
    OnlineStore
      .from(string)
      .getOrElse(
        throw new RuntimeException(s"Unable to decode online store = $string")
      )
  }

  implicit val storeProductEncoder: Encoder[StoreProduct] = deriveEncoder[StoreProduct]
  implicit val storeProductDecoder: Decoder[StoreProduct] = deriveDecoder[StoreProduct]

  implicit val productDetailsEncoder: Encoder[ProductDetails] = deriveEncoder[ProductDetails]
  implicit val productDetailsDecoder: Decoder[ProductDetails] = deriveDecoder[ProductDetails]

  implicit val storedProductStatusEncoder: Encoder[StoredProduct.Status] = Encoder.instance(_.string.asJson)

  implicit val storedProductStatusDecoder: Decoder[StoredProduct.Status] = Decoder.decodeString.map { string =>
    StoredProduct.Status
      .from(string)
      .getOrElse(
        throw new RuntimeException(s"Unable to decode status = $string")
      )
  }
  implicit val storedProductEncoder: Encoder[StoredProduct] = deriveEncoder[StoredProduct]
  implicit val storedProductDecoder: Decoder[StoredProduct] = deriveDecoder[StoredProduct]

  implicit val storedDataEncoder: Encoder[StoredData] = deriveEncoder[StoredData]
  implicit val storedDataDecoder: Decoder[StoredData] = deriveDecoder[StoredData]
}
