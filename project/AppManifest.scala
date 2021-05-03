import chrome.permissions.Permission
import chrome.permissions.Permission.API
import chrome.{Background, BrowserAction, ContentScript, ExtensionManifest}
import com.alexitc.Chrome
import sbt.Keys

object AppManifest {

  def generate(appName: String, appVersion: String): ExtensionManifest = {
    new ExtensionManifest {
      override val name = appName
      override val version = appVersion

      override val description = Some(
        "Allows you to follow the items you are interested to buy if they were cheaper, notifying you once their price decreases"
      )
      require(!description.exists(_.length > 132), "Chrome allows upto 132 characters in the description")

      override val icons = Chrome.icons("icons", "app.png", Set(48, 96))

      override val permissions = Set[Permission](
        API.Storage,
        API.Notifications,
        API.Alarms
      )

      override val defaultLocale: Option[String] = Some("en")

      override val browserAction: Option[BrowserAction] =
        Some(BrowserAction(icons, Some(appName), Some("popup.html")))

      // scripts used on all modules
      val commonScripts = List("scripts/common.js", "main-bundle.js")

      override val background = Background(
        scripts = commonScripts ::: List("scripts/background-script.js")
      )

      override val contentScripts: List[ContentScript] = List(
        ContentScript(
          matches = List(
            "https://www.liverpool.com.mx/*",
            "https://www.zara.com/*",
            "https://www2.hm.com/*",
            "https://m2.hm.com/*",
            "https://www.coppel.com/*",
            "https://www.mercadolibre.com.mx/*",
            "https://articulo.mercadolibre.com.mx/*",
            "https://www.costco.com.mx/*",
            "https://www.costco.com/*",
            "https://www.homedepot.com/*",
            "https://www.homedepot.com.mx/*",
            "http://www.homedepot.com.mx/*",
            "https://www.ebay.com/itm/*",
            "https://www.officedepot.com.mx/*",
            "https://www.officedepot.com/*",
            "https://www.sanborns.com.mx/*",
            "https://www.sams.com.mx/*",
            "https://www.samsclub.com/*",
            "https://shop.nordstrom.com/s/*",
            "https://www.elektra.com.mx/*",
            "https://www.costco.com.mx/*",
            "https://www.costco.com/*",
            "https://www.walmart.com.mx/*",
            "https://super.walmart.com.mx/*",
            "https://www.bestbuy.com.mx/*",
            "https://www.bestbuy.com/*",
            "https://www.elpalaciodehierro.com/*",
            "https://www.amazon.com.mx/*"
          ),
          css = List(),
          js = commonScripts ::: List("external-scripts/sidenavCzd.js", "scripts/active-tab-script.js")
        )
      )
      override val webAccessibleResources = List("icons/*")
    }
  }
}
