package net.cazadescuentos.background.alarms

import java.util.UUID

import chrome.alarms.bindings.AlarmInfo
import net.cazadescuentos.background.alarms.ProductUpdaterAlarm._
import net.cazadescuentos.background.services.ProductUpdaterService

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

private[background] class ProductUpdaterAlarm(
    config: Config,
    productUpdaterService: ProductUpdaterService
)(implicit ec: ExecutionContext) {

  def register(buyerId: UUID): Unit = {
    val alarmName = "CAZADESCUENTOS_TIME_TO_UPDATE_PRODUCTS"
    chrome.alarms.Alarms.create(alarmName, AlarmInfo(delayInMinutes = 1.0, periodInMinutes = config.periodInMinutes))
    chrome.alarms.Alarms.onAlarm.filter(_.name == alarmName).listen { alarm =>
      log(s"Got alarm: ${alarm.name}")
      run(buyerId)
    }
  }

  private def run(buyerId: UUID): Unit = {
    val result = productUpdaterService.updateThemAll(buyerId)

    result.onComplete {
      case Failure(ex) => log(s"Failed to update products: ${ex.getMessage}")
      case Success(_) => ()
    }
  }

  private def log(msg: String): Unit = {
    println(s"productUpdaterAlarm: $msg")
  }
}

object ProductUpdaterAlarm {
  case class Config(periodInMinutes: Double)
}
