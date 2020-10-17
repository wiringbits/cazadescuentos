package net.cazadescuentos.background.services.http

import net.wiringbits.cazadescuentos.api.storage.models.StoredData

import scala.concurrent.{ExecutionContext, Future}

class HttpMigrationService(productHttpService: ProductHttpService)(implicit ec: ExecutionContext) {

  def migrate(data: StoredData): Future[Unit] = {
    productHttpService
      .getAll(data.buyerId)
      .map { httpProducts =>
        val notInHttp = data.products.filter { sp =>
          !httpProducts.exists(_.storeProduct == sp.storeProduct)
        }

        notInHttp
      }
      .map { toStore =>
        toStore.foldLeft(Future.unit) {
          case (acc, cur) =>
            acc.flatMap { _ =>
              productHttpService
                .create(data.buyerId, cur.storeProduct)
                .transformWith(_ => Future.unit)
            }
        }
      }
  }
}
