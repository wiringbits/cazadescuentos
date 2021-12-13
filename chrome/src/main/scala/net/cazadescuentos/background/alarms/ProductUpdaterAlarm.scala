package net.cazadescuentos.background.alarms

import java.util.UUID

import chrome.alarms.bindings.AlarmInfo
import net.cazadescuentos.background.alarms.ProductUpdaterAlarm._
import net.cazadescuentos.background.services.browser.BrowserNotificationService
import net.wiringbits.cazadescuentos.api.http.ProductHttpService

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import scala.util.{Failure, Success}

private[background] class ProductUpdaterAlarm(
    config: Config,
    notificationService: BrowserNotificationService,
    productHttpService: ProductHttpService
) {

  def register(buyerId: UUID): Unit = {
    val alarmName = "CAZADESCUENTOS_TIME_TO_UPDATE_PRODUCTS"
    chrome.alarms.Alarms.create(alarmName, AlarmInfo(delayInMinutes = 1.0, periodInMinutes = config.periodInMinutes))
    chrome.alarms.Alarms.onAlarm.filter(_.name == alarmName).listen { alarm =>
      log(s"Got alarm: ${alarm.name}")
      run(buyerId)
    }
  }

  private def run(buyerId: UUID): Unit = {
    productHttpService
      .notifications(buyerId)
      .onComplete {
        case Failure(ex) => log(s"Failed to update products: ${ex.getMessage}")
        case Success(response) =>
          response.data.headOption.foreach { lastNotification =>
            notificationService.notify(lastNotification.message)
          }
      }
  }

  private def log(msg: String): Unit = {
    println(s"productUpdaterAlarm: $msg")
  }
}

object ProductUpdaterAlarm {
  case class Config(periodInMinutes: Double)
}
