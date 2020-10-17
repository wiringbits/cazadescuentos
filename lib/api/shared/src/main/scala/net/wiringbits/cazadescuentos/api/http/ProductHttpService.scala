package net.wiringbits.cazadescuentos.api.http

import java.util.UUID

import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import net.wiringbits.cazadescuentos.api.codecs.CirceCodecs._
import net.wiringbits.cazadescuentos.api.http.models.{NotificationsSubscription, ProductDetails}
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import sttp.client._
import sttp.model.MediaType

import scala.concurrent.{ExecutionContext, Future}

trait ProductHttpService {
  def getAll(): Future[List[ProductDetails]]
  def getAllSummary(): Future[List[ProductDetails]]
  def create(storeProduct: StoreProduct): Future[ProductDetails]
  def delete(storeProduct: StoreProduct): Future[Unit]
  def subscribe(subscription: NotificationsSubscription): Future[Unit]
}

object ProductHttpService {

  case class Config(serverUrl: String, buyerId: UUID)

  class DefaultImpl(config: ProductHttpService.Config)(
      implicit backend: SttpBackend[Future, Nothing, Nothing],
      ec: ExecutionContext
  ) extends ProductHttpService {

    private val buyerId = config.buyerId

    private val ServerAPI = sttp.model.Uri
      .parse(config.serverUrl)
      .getOrElse(throw new RuntimeException("Invalid server url"))

    override def getAll(): Future[List[ProductDetails]] = {
      val path = ServerAPI.path :+ "products"
      val uri = ServerAPI.path(path)
      basicRequest
        .header("Authorization", buyerId.toString)
        .get(uri)
        .response(asString)
        .send()
        .map(_.body)
        .map {
          case Left(error) =>
            throw new RuntimeException(s"Request failed: $error")
          case Right(response) =>
            parse(response)
              .flatMap(_.as[List[ProductDetails]])
              .getOrElse(
                throw new RuntimeException(
                  s"Failed to decode response from the server: $response"
                )
              )
        }
    }

    override def getAllSummary(): Future[List[ProductDetails]] = {
      val path = ServerAPI.path ++ Seq("products", "summary")
      val uri = ServerAPI.path(path)
      basicRequest
        .header("Authorization", buyerId.toString)
        .get(uri)
        .response(asString)
        .send()
        .map(_.body)
        .map {
          case Left(error) =>
            throw new RuntimeException(s"Request failed: $error")
          case Right(response) =>
            parse(response)
              .flatMap(_.as[List[ProductDetails]])
              .getOrElse(
                throw new RuntimeException(
                  s"Failed to decode response from the server: $response"
                )
              )
        }
    }

    override def create(storeProduct: StoreProduct): Future[ProductDetails] = {
      val path = ServerAPI.path ++ Seq(
        "products"
      )

      val body = s"""{ "store": "${storeProduct.store.id}", "storeProductId": "${storeProduct.id}" }"""
      val uri = ServerAPI.path(path)
      basicRequest
        .header("Authorization", buyerId.toString)
        .contentType(MediaType.ApplicationJson)
        .post(uri)
        .body(body)
        .response(asString)
        .send()
        .map(_.body)
        .map {
          case Left(error) =>
            throw new RuntimeException(s"Request failed: $error")
          case Right(response) =>
            parse(response)
              .flatMap(_.as[ProductDetails])
              .getOrElse(
                throw new RuntimeException(
                  s"Failed to decode response from the server: $response"
                )
              )
        }
    }

    override def delete(storeProduct: StoreProduct): Future[Unit] = {
      val path = ServerAPI.path ++ Seq(
        "products"
      )

      val body = s"""{ "store": "${storeProduct.store.id}", "storeProductId": "${storeProduct.id}", "delete": true }"""
      val uri = ServerAPI.path(path)
      basicRequest
        .header("Authorization", buyerId.toString)
        .contentType(MediaType.ApplicationJson)
        .put(uri)
        .body(body)
        .response(asString)
        .send()
        .map(_.body)
        .map {
          case Left(error) => throw new RuntimeException(s"Request failed: $error")
          case Right(_) => ()
        }
    }

    override def subscribe(subscription: NotificationsSubscription): Future[Unit] = {
      val path = ServerAPI.path ++ Seq(
        "notifications",
        "subscribe"
      )

      val body = subscription.asJson
      val uri = ServerAPI.path(path)
      basicRequest
        .header("Authorization", buyerId.toString)
        .contentType(MediaType.ApplicationJson)
        .put(uri)
        .body(body.toString())
        .response(asString)
        .send()
        .map(_.body)
        .map {
          case Left(error) => throw new RuntimeException(s"Failed to subscribe: $error")
          case Right(_) => ()
        }
    }
  }
}
