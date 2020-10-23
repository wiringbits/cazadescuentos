package net.wiringbits.cazadescuentos.api.http.models

import java.time.Instant

import net.wiringbits.cazadescuentos.common.models._

case class GetTrackedProductsResponse(data: List[GetTrackedProductsResponse.TrackedProduct])

object GetTrackedProductsResponse {
  case class TrackedProduct(
      id: TrackedProductId,
      store: OnlineStore,
      storeProductId: StoreProductId,
      name: String,
      currency: ProductCurrency,
      initialPrice: BigDecimal,
      lastPrice: BigDecimal,
      availabilityStatus: AvailabilityStatus,
      createdAt: Instant,
      successfullyUpdatedAt: Instant
  ) {
    // TODO: Avoid these methods as they are just repeated
    def storeProduct: StoreProduct = StoreProduct(storeProductId.string, store)
    def url: String = store.url(storeProductId.string)

    // TODO: Avoid casting to double: https://github.com/scala-js/scala-js/issues/3407
    def formattedLastPrice: String = s"${currency.string}%.2f".format(lastPrice.toDouble)
    def formattedInitialPrice: String = s"${currency.string}%.2f".format(initialPrice.toDouble)

    def discountPercent: Int = {
      100 - (lastPrice * 100 / initialPrice).toInt
    }
  }
}
