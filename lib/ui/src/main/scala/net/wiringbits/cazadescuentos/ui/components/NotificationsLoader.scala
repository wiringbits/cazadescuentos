package net.wiringbits.cazadescuentos.ui.components

import net.wiringbits.cazadescuentos.api.http.models.GetNotificationsResponse
import slinky.core.annotations.react

@react object NotificationsLoader extends RemoteDataLoaderBase[GetNotificationsResponse] {
  val component = theComponent
}
