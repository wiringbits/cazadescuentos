package net.wiringbits.cazadescuentos.common.storage

import io.circe.parser.parse
import io.circe.syntax._
import net.wiringbits.cazadescuentos.api.codecs.CirceCodecs._
import net.wiringbits.cazadescuentos.api.storage.models.StoredData
import org.scalajs.dom

class StorageService {

  import StorageService._

  def load(): Option[StoredData] = {
    val json = dom.window.localStorage.getItem(Key)

    parse(json).toOption
      .flatMap(_.as[StoredData].toOption)
  }

  def unsafeSet(data: StoredData): Unit = {
    val json = data.asJson.noSpaces

    dom.window.localStorage.setItem(Key, json)
  }
}

object StorageService {

  private val Key = "data"
}
