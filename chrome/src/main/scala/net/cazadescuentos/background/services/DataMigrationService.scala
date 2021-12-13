package net.cazadescuentos.background.services

import java.util.UUID

import net.cazadescuentos.background.services.storage.StorageMigrationService

import scala.concurrent.Future
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

class DataMigrationService(
    storageMigrationService: StorageMigrationService
) {

  def migrate(): Future[UUID] = {
    for {
      data <- storageMigrationService.migrate()
    } yield data.buyerId
  }
}
