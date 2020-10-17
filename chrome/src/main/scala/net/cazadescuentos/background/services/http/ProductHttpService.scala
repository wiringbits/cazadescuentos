package net.cazadescuentos.background.services.http

import java.util.UUID

import io.circe.parser.parse
import net.cazadescuentos.background.services.http.ProductHttpService._
import net.wiringbits.cazadescuentos.api.codecs.CirceCodecs._
import net.wiringbits.cazadescuentos.api.http.models.ProductDetails
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import sttp.client._
import sttp.model.MediaType

import scala.concurrent.{ExecutionContext, Future}

// NOTE: This can't be taken from the common library until it receives the buyer as argument
private[background] class ProductHttpService(config: Config)(
    implicit backend: SttpBackend[Future, Nothing, Nothing],
    ec: ExecutionContext
) {

  private val ServerAPI = sttp.model.Uri
    .parse(config.serverUrl)
    .getOrElse(throw new RuntimeException("Invalid server url"))

  def getAll(buyerId: UUID): Future[List[ProductDetails]] = {
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

  def getAllSummary(buyerId: UUID): Future[List[ProductDetails]] = {
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

  def create(buyerId: UUID, storeProduct: StoreProduct): Future[ProductDetails] = {
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

  def delete(buyerId: UUID, storeProduct: StoreProduct): Future[Unit] = {
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
        case Right(response) => ()
      }
  }
}

object ProductHttpService {

  case class Config(serverUrl: String)

  def apply(
      config: Config
  )(implicit ec: ExecutionContext): ProductHttpService = {
    val backend = FetchBackend()
    new ProductHttpService(config)(backend, ec)
  }
}
