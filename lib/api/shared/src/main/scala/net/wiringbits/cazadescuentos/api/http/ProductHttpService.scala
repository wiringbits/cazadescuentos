package net.wiringbits.cazadescuentos.api.http

import java.util.UUID

import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import net.wiringbits.cazadescuentos.api.codecs.CirceCodecs._
import net.wiringbits.cazadescuentos.api.http.models._
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import sttp.client._
import sttp.model.MediaType

import scala.concurrent.{ExecutionContext, Future}

trait ProductHttpService {
  def getAll(buyerId: UUID): Future[List[ProductDetails]]
  def getAllSummary(buyerId: UUID): Future[List[ProductDetails]]
  def getAllSummaryV2(buyerId: UUID): Future[GetTrackedProductsResponse]
  def create(buyerId: UUID, storeProduct: StoreProduct): Future[ProductDetails]
  def delete(buyerId: UUID, storeProduct: StoreProduct): Future[Unit]
  def subscribe(buyerId: UUID, subscription: NotificationsSubscription): Future[Unit]
  def bestDiscounts(buyerId: UUID): Future[GetDiscountsResponse]
  def notifications(buyerId: UUID): Future[GetNotificationsResponse]
  def notificationRead(buyerId: UUID, id: UUID): Future[Unit]
}

object ProductHttpService {

  case class Config(serverUrl: String)

  private def asJson[R: Decoder] = {
    asString
      .map {
        case Right(string) => parse(string).left.map(_.message)
        case Left(error) => Left(error)
      }
      .map {
        case Right(json) =>
          val result = json.as[R]
          result.left.map(_.message)

        case Left(error) => Left(error)
      }
  }

  implicit class SttpExt[T](val future: Future[Either[String, T]]) {

    def expectSuccess(implicit ec: ExecutionContext): Future[T] = {
      future
        .map {
          case Left(error) => throw new RuntimeException(error)
          case Right(response) => response
        }
    }
  }

  class DefaultImpl(config: ProductHttpService.Config)(
      implicit backend: SttpBackend[Future, Nothing, Nothing],
      ec: ExecutionContext
  ) extends ProductHttpService {

    private val ServerAPI = sttp.model.Uri
      .parse(config.serverUrl)
      .getOrElse(throw new RuntimeException("Invalid server url"))

    private def prepareRequest[R: Decoder](buyerId: UUID) = {
      basicRequest
        .header("Authorization", buyerId.toString)
        .contentType(MediaType.ApplicationJson)
        .response(asJson[R])
    }

    override def getAll(buyerId: UUID): Future[List[ProductDetails]] = {
      val path = ServerAPI.path :+ "products"
      val uri = ServerAPI.path(path)
      prepareRequest[List[ProductDetails]](buyerId)
        .get(uri)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def getAllSummary(buyerId: UUID): Future[List[ProductDetails]] = {
      val path = ServerAPI.path ++ Seq("products", "summary")
      val uri = ServerAPI.path(path)

      prepareRequest[List[ProductDetails]](buyerId)
        .get(uri)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def getAllSummaryV2(buyerId: UUID): Future[GetTrackedProductsResponse] = {
      val path = ServerAPI.path ++ Seq("v2", "products")
      val uri = ServerAPI.path(path)
      prepareRequest[GetTrackedProductsResponse](buyerId: UUID)
        .get(uri)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def create(buyerId: UUID, storeProduct: StoreProduct): Future[ProductDetails] = {
      val path = ServerAPI.path ++ Seq(
        "products"
      )
      val uri = ServerAPI.path(path)
      val body = s"""{ "store": "${storeProduct.store.id}", "storeProductId": "${storeProduct.id}" }"""

      prepareRequest[ProductDetails](buyerId)
        .post(uri)
        .body(body)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def delete(buyerId: UUID, storeProduct: StoreProduct): Future[Unit] = {
      val path = ServerAPI.path ++ Seq(
        "products"
      )

      val body = s"""{ "store": "${storeProduct.store.id}", "storeProductId": "${storeProduct.id}", "delete": true }"""
      val uri = ServerAPI.path(path)
      prepareRequest[Unit](buyerId: UUID)
        .put(uri)
        .body(body)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def subscribe(buyerId: UUID, subscription: NotificationsSubscription): Future[Unit] = {
      val path = ServerAPI.path ++ Seq(
        "notifications",
        "subscribe"
      )

      val body = subscription.asJson
      val uri = ServerAPI.path(path)
      prepareRequest[Unit](buyerId)
        .put(uri)
        .body(body.toString())
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def bestDiscounts(buyerId: UUID): Future[GetDiscountsResponse] = {
      val path = ServerAPI.path ++ Seq("best-discounts")
      val uri = ServerAPI.path(path)
      prepareRequest[GetDiscountsResponse](buyerId)
        .get(uri)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def notifications(buyerId: UUID): Future[GetNotificationsResponse] = {
      val path = ServerAPI.path ++ Seq("notifications")
      val uri = ServerAPI.path(path)
      prepareRequest[GetNotificationsResponse](buyerId)
        .get(uri)
        .send()
        .map(_.body)
        .expectSuccess
    }

    override def notificationRead(buyerId: UUID, id: UUID): Future[Unit] = {
      val path = ServerAPI.path ++ Seq("notifications", id.toString)
      val uri = ServerAPI.path(path)
      prepareRequest[Unit](buyerId)
        .delete(uri)
        .send()
        .map(_.body)
        .expectSuccess
    }
  }
}
