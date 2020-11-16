package net.cazadescuentos.background.services.storage

import java.util.UUID

import net.wiringbits.cazadescuentos.api.storage.models.StoredData

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
            .flatMap { _ =>
              val data = StoredData(buyerId)
              storageService.unsafeSet(data).map(_ => data)
            }
      }
  }
}
