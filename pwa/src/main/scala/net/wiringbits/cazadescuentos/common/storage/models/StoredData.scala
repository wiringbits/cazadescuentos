package net.wiringbits.cazadescuentos.common.storage.models

import java.util.UUID

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class StoredData(buyerId: UUID, products: List[StoredProduct])

object StoredData {

  implicit val encoder: Encoder[StoredData] = deriveEncoder[StoredData]
  implicit val decoder: Decoder[StoredData] = deriveDecoder[StoredData]
}
