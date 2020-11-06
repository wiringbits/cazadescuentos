package net.wiringbits.cazadescuentos.api.http.models

import java.time.Instant
import java.util.UUID

import net.wiringbits.cazadescuentos.common.models.{OnlineStore, ProductCurrency, StoreProductId}

case class GetDiscountsResponse(data: List[GetDiscountsResponse.Discount])

object GetDiscountsResponse {
  case class Discount(
      id: UUID,
      store: OnlineStore,
      storeProductId: StoreProductId,
      name: String,
      currency: ProductCurrency,
      initialPrice: String,
      discountPrice: String,
      discountAmount: String,
      discountPercentage: Int,
      createdAt: Instant,
      url: String
  )
}
