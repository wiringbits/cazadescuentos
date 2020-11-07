package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse
import net.wiringbits.cazadescuentos.ui.components.RemoteDataLoaderBase
import slinky.core.annotations.react

@react object DiscountsDataLoader extends RemoteDataLoaderBase[GetDiscountsResponse] {
  val component = theComponent
}
