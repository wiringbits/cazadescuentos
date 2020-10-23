package net.wiringbits.cazadescuentos.api.codecs

import java.util.UUID

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import net.wiringbits.cazadescuentos.api.http.models.{GetTrackedProductsResponse, ProductDetails}
import net.wiringbits.cazadescuentos.api.storage.models.{StoredData, StoredProduct}
import net.wiringbits.cazadescuentos.common.models._

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

  implicit val availabilityStatusDecoder: Decoder[AvailabilityStatus] = implicitly[Decoder[String]].emap { str =>
    AvailabilityStatus.from(str).toRight(s"Unknown status: $str")
  }

  implicit val trackedProductIdDecoder: Decoder[TrackedProductId] =
    implicitly[Decoder[UUID]].map(TrackedProductId.apply)
  implicit val storeProductIdDecoder: Decoder[StoreProductId] = implicitly[Decoder[String]].map(StoreProductId.apply)
  implicit val productCurrencyDecoder: Decoder[ProductCurrency] = implicitly[Decoder[String]].map(ProductCurrency.apply)

  implicit val getTrackedProductsResponseTrackedProductDecoder: Decoder[GetTrackedProductsResponse.TrackedProduct] = {
    deriveDecoder[GetTrackedProductsResponse.TrackedProduct]
  }

}
