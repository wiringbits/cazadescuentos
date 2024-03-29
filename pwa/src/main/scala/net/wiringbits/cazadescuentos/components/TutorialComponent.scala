package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.mod.FlexDirectionProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.detectBrowser.mod.Browser
import com.alexitc.materialui.facade.materialUiCore.{
  typographyTypographyMod,
  components => mui,
  materialUiCoreStrings => muiStrings
}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.ui.components.{Subtitle, TextLine, Title, YoutubeVideo}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.Fragment

@react object TutorialComponent {
  case class Props(api: API, appInfo: AppInfo)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "tutorial" -> CSSProperties()
          .setPadding(16)
          .setBorderRadius(8)
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  private val globalStores =
    List("https://www2.hm.com", "https://m2.hm.com/m", "https://www.zara.com", "https://www.ebay.com")
  private val mexicoStores =
    List(
      "https://www.liverpool.com.mx",
      "https://www.coppel.com",
      "https://www.officedepot.com.mx",
      "https://www.sanborns.com.mx",
      "https://www.sams.com.mx",
      "https://www.mercadolibre.com.mx",
      "https://www.elektra.com.mx",
      "https://www.costco.com.mx",
      "https://www.homedepot.com.mx",
      "https://www.walmart.com.mx",
      "https://www.bestbuy.com.mx",
      "https://www.elpalaciodehierro.com"
    )
  private val usaStores = List(
    "https://www.officedepot.com",
    "https://www.samsclub.com",
    "https://shop.nordstrom.com",
    "https://www.bestbuy.com"
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    Fragment(
      Title("Ayuda"),
      mui.Paper(className := classes("tutorial"))(
        Subtitle("Acerca de"),
        TextLine("El propósito Cazadescuentos, ayudarte a ahorrar mientras compras por Internet:"),
        TextLine(
          "Si deseas comprar un producto, pero el precio no te convence, agrégalo a nuestra app y esta te notificara cuando baje de precio."
        ),
        br(),
        TextLine(
          "Si acabas de comprar un producto, pagando con una tarjeta que ofrece garantía de precios, agrégalo a nuestra app y esta te notificara si baja de precio para que te puedan reembolsar la diferencia de precios."
        ),
        br(),
        TextLine(
          "Solo visita el producto que te interesa en alguna de las tiendas soportadas, copia la URL y agregalo a cazadescuentos, la app lo seguirá por ti, notificandote cuando este baja de precio."
        ),
        br(),
        TextLine(
          "Se recomienda instalar la app de Android, y compartir los productos que te interesan con esta app"
        ),
        TextLine("Puedes ver el siguiente video para entender como:"),
        YoutubeVideo("42FoH_zhvyI"),
        AndroidLinkButton(),
        br(),
        Subtitle("Tiendas soportadas"),
        SupportedStoreList("Globales", globalStores),
        SupportedStoreList("México", mexicoStores),
        SupportedStoreList("USA", usaStores)
      )
    )
  }
}
