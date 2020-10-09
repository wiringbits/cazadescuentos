package net.cazadescuentos.background.services.storage

import java.util.UUID

import net.cazadescuentos.background.services.storage.models.StoredData

import scala.concurrent.{ExecutionContext, Future}

class StorageMigrationService(storageService: StorageService, legacyStorageService: ProductStorageService)(
    implicit ec: ExecutionContext
) {

  def migrate(): Future[StoredData] = {
    storageService
      .load()
      .flatMap {
        case Some(data) =>
          println("data migration not required")
          // migration not required
          Future.successful(data)

        case None =>
          val buyerId = UUID.randomUUID()
          println("migrating data")
          legacyStorageService
            .getAll()
            .flatMap { products =>
              val data = StoredData(buyerId, products)
              storageService.unsafeSet(data).map(_ => data)
            }
      }
  }
}
