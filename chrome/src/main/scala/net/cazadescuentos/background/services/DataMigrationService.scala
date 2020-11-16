package net.cazadescuentos.background.services

import java.util.UUID

import net.cazadescuentos.background.services.storage.StorageMigrationService

import scala.concurrent.{ExecutionContext, Future}

class DataMigrationService(
    storageMigrationService: StorageMigrationService
)(
    implicit ec: ExecutionContext
) {

  def migrate(): Future[UUID] = {
    for {
      data <- storageMigrationService.migrate()
    } yield data.buyerId
  }
}
