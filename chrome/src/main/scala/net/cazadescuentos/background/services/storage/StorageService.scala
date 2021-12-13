package net.cazadescuentos.background.services.storage

import io.circe.parser.parse
import io.circe.syntax._
import net.wiringbits.cazadescuentos.api.codecs.CirceCodecs._
import net.wiringbits.cazadescuentos.api.storage.models.StoredData

import scala.concurrent.Future
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import scala.scalajs.js

private[background] class StorageService {

  import StorageService._

  def load(): Future[Option[StoredData]] = {
    chrome.storage.Storage.local
      .get(Key: js.Any)
      .map(_.asInstanceOf[js.Dictionary[String]])
      .map { dict =>
        val json = dict.getOrElse(Key, "{}")
        parse(json).toOption
          .flatMap(_.as[StoredData].toOption)
      }
  }

  def unsafeSet(data: StoredData): Future[Unit] = {
    val json = data.asJson.noSpaces
    val dict = js.Dictionary(Key -> js.Any.fromString(json))

    chrome.storage.Storage.local.set(dict)
  }
}

object StorageService {

  private val Key = "data"
}
