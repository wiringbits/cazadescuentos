package net.wiringbits.cazadescuentos.api.http.models

import net.wiringbits.cazadescuentos.common.models.{OnlineStore, StoreProduct}

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
