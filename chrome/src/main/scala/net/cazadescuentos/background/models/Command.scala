package net.cazadescuentos.background.models

import io.circe.generic.auto._
import io.circe.parser.parse
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

  def decode(string: String): Try[Command] = {
    parse(string).toTry
      .flatMap(_.as[Command].toTry)
  }
}
