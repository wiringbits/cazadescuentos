package net.cazadescuentos.background.models

import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import net.wiringbits.cazadescuentos.common.models.StoreProduct

import scala.util.Try

private[background] sealed trait Command extends Product with Serializable

private[background] object Command {

  final case class FindBuyerId() extends Command
  final case class TrackProduct(product: StoreProduct) extends Command
  final case class FindStoredProduct(id: StoreProduct) extends Command
  final case class DeleteProduct(id: StoreProduct) extends Command
  final case class GetStoredProductsSummary() extends Command
  final case class SendBrowserNotification(title: String, message: String) extends Command

  implicit val encoder: Encoder[Command] = Encoder.instance { cmd =>
    val (tpe, json) = cmd match {
      case cmd: FindBuyerId => "FindBuyerId" -> cmd.asJson
      case cmd: TrackProduct => "TrackProduct" -> cmd.asJson
      case cmd: FindStoredProduct => "FindStoredProduct" -> cmd.asJson
      case cmd: DeleteProduct => "DeleteProduct" -> cmd.asJson
      case cmd: GetStoredProductsSummary => "GetStoredProductsSummary" -> cmd.asJson
      case cmd: SendBrowserNotification =>
        "SendBrowserNotification" -> cmd.asJson
    }

    Json.obj("type" -> tpe.asJson, "value" -> json)
  }

  implicit val decoder: Decoder[Command] = Decoder.instance { json =>
    for {
      tpe <- json.get[String]("type")
      value <- json.get[Json]("value")
      cmd <- tpe match {
        case "FindBuyerId" => value.as[FindBuyerId]
        case "TrackProduct" => value.as[TrackProduct]
        case "FindStoredProduct" => value.as[FindStoredProduct]
        case "DeleteProduct" => value.as[DeleteProduct]
        case "GetStoredProductsSummary" => value.as[GetStoredProductsSummary]
        case "SendBrowserNotification" => value.as[SendBrowserNotification]
        case _ =>
          Left(
            DecodingFailure.fromThrowable(
              new RuntimeException("Unknown Command"),
              List.empty
            )
          )
      }
    } yield cmd
  }

  def decode(string: String): Try[Command] = {
    parse(string).toTry
      .flatMap(_.as[Command].toTry)
  }
}
