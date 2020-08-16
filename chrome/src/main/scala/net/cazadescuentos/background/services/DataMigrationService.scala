package net.cazadescuentos.background.services

import java.util.UUID

import net.cazadescuentos.background.services.http.HttpMigrationService
import net.cazadescuentos.background.services.storage.{ProductStorageService, StorageMigrationService}

import scala.concurrent.{ExecutionContext, Future}

class DataMigrationService(
    storageMigrationService: StorageMigrationService,
    httpMigrationService: HttpMigrationService,
    legacyStorageService: ProductStorageService
)(
    implicit ec: ExecutionContext
) {

  def migrate(): Future[UUID] = {
    for {
      data <- storageMigrationService.migrate()
      _ <- httpMigrationService.migrate(data)
      _ <- legacyStorageService.clear()
    } yield data.buyerId
  }
}
