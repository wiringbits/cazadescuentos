package net.cazadescuentos.background.services.browser

import net.cazadescuentos.common.I18NMessages
import net.cazadescuentos.facades.CommonsFacade

private[background] class BrowserNotificationService(messages: I18NMessages) {

  def notify(message: String): Unit = {
    notify(messages.appName, message)
  }

  def notify(title: String, message: String): Unit = {

    /**
     * Sadly, scala-js-chrome fails when creating notifications on firefox, to overcome that issue, we expect a
     * simple JavaScript function that creates notifications which works on Firefox and Chrome, the facade
     * just invoke that function (see common.js)
     */
    CommonsFacade.notify(
      title,
      message,
      chrome.runtime.Runtime.getURL("icons/96/app.png")
    )
  }
}
