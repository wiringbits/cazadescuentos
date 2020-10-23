package net.wiringbits.cazadescuentos.common.models

sealed abstract class AvailabilityStatus(val string: String)

object AvailabilityStatus {
  case object Available extends AvailabilityStatus("Available")
  case object Unavailable extends AvailabilityStatus("Unavailable")

  val values = List(Available, Unavailable)

  def from(string: String): Option[AvailabilityStatus] = {
    values.find(_.string equalsIgnoreCase string)
  }
}
