package net.wiringbits.cazadescuentos.api.storage.models

import java.util.UUID

case class StoredData(buyerId: UUID, products: List[StoredProduct])
