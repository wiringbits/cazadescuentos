package net.cazadescuentos.background.models

import java.util.UUID

import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import net.cazadescuentos.models.{StoreProduct, StoredProduct}

import scala.util.Try

private[background] sealed trait Event extends Product with Serializable

private[background] object Event {

  final case class BuyerIdFound(buyerId: UUID) extends Event
  final case class ProductTracked(product: StoreProduct) extends Event
  final case class FoundStoredProduct(result: Option[StoredProduct]) extends Event
  final case class GotStoredProducts(products: List[StoredProduct]) extends Event
  final case class StoredProductDeleted(id: StoreProduct) extends Event
  final case class BrowserNotificationSent() extends Event
  final case class CommandRejected(reason: String) extends Event

  implicit val encoder: Encoder[Event] = Encoder.instance { event =>
    val (tpe, json) = event match {
      case e: BuyerIdFound => "BuyerIdFound" -> e.asJson
      case e: ProductTracked => "ProductTracked" -> e.asJson
      case e: FoundStoredProduct => "FoundStoredProduct" -> e.asJson
      case e: StoredProductDeleted => "StoredProductDeleted" -> e.asJson
      case e: GotStoredProducts => "GotStoredProducts" -> e.asJson
      case e: BrowserNotificationSent =>
        "BrowserNotificationSent" -> e.asJson
      case e: CommandRejected => "CommandRejected" -> e.asJson
    }

    Json.obj("type" -> tpe.asJson, "value" -> json)
  }

  implicit val decoder: Decoder[Event] = Decoder.instance { json =>
    for {
      tpe <- json.get[String]("type")
      value <- json.get[Json]("value")
      cmd <- tpe match {
        case "BuyerIdFound" => value.as[BuyerIdFound]
        case "ProductTracked" => value.as[ProductTracked]
        case "FoundStoredProduct" => value.as[FoundStoredProduct]
        case "StoredProductDeleted" => value.as[StoredProductDeleted]
        case "GotStoredProducts" => value.as[GotStoredProducts]
        case "BrowserNotificationSent" => value.as[BrowserNotificationSent]
        case "CommandRejected" => value.as[CommandRejected]
        case _ =>
          Left(
            DecodingFailure
              .fromThrowable(new RuntimeException("Unknown Event"), List.empty)
          )
      }
    } yield cmd
  }

  def decode(string: String): Try[Event] = {
    parse(string).toTry
      .flatMap(_.as[Event].toTry)
  }
}
