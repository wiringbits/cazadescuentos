package net.cazadescuentos.common

import chrome.i18n.I18N
import net.cazadescuentos.models.OnlineStore

class I18NMessages {

  def appName: String = getMessage("extensionName")

  def yes: String = getMessage("yes")
  def no: String = getMessage("no")

  def questionBeforeFollowingItem: String =
    getMessage("questionBeforeFollowingItem")

  def youAreFollowingThisItem: String = getMessage("youAreFollowingThisItem")

  def productHasDiscountNotification(product: String, store: OnlineStore, droppedPercent: Int): String = {
    getMessage(
      "productHasDiscountNotification",
      product,
      store.name,
      droppedPercent.toString
    )
  }

  def dropListEmpty: String = getMessage("dropListEmpty")

  def dropListEmptyDetailedMessage: String =
    getMessage("dropListEmptyDetailedMessage")

  def labelStore: String = getMessage("labelStore")
  def labelProduct: String = getMessage("labelProduct")
  def labelPrice: String = getMessage("labelPrice")
  def labelAvailable: String = getMessage("labelAvailable")
  def labelDiscount: String = getMessage("labelDiscount")
  def labelInitialPrice: String = getMessage("labelInitialPrice")
  def labelActions: String = getMessage("labelActions")

  def followedItemsLimitReachedMessage: String = getMessage("followedItemsLimitReachedMessage")

  private def getMessage(id: String, substitutions: String*): String = {
    I18N
      .getMessage(id, substitutions: _*)
      .getOrElse(throw new RuntimeException(s"Message $id not available"))
  }
}
