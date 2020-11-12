package net.wiringbits.cazadescuentos.api.http.models

import java.time.Instant
import java.util.UUID

case class GetNotificationsResponse(data: List[GetNotificationsResponse.Notification])

object GetNotificationsResponse {
  case class Notification(
      id: UUID,
      message: String,
      createdAt: Instant
  )
}
