package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.api.http.models.GetTrackedProductsResponse
import net.wiringbits.cazadescuentos.ui.components.RemoteDataLoaderBase
import slinky.core.annotations.react

@react object MyProductsSummaryDataLoader extends RemoteDataLoaderBase[GetTrackedProductsResponse] {
  val component = theComponent
}
