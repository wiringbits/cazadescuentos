package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.{FunctionalComponent, _}
import slinky.core.annotations.react
import slinky.web.html._
import typings.detectBrowser.mod.Browser
import com.alexitc.materialui.facade.materialUiCore.{components => mui}

@react object TutorialComponent {

  case class Props(api: API, appInfo: AppInfo)

  private val firefoxLink = {
    a(href := "https://addons.mozilla.org/firefox/addon/cazadescuentos/", target := "_blank")(
      img(
        height := "40",
        src := "https://findicons.com/files/icons/783/mozilla_pack/128/firefox.png"
      )
    )
  }

  private val chromeLink = {
    a(
      href := "https://chrome.google.com/webstore/detail/cazadescuentos/miadcmhlfknbjhlknpaidjnelinghpdf",
      target := "_blank"
    )(
      img(
        height := "40",
        src := "https://icons.iconarchive.com/icons/dtafalonso/android-l/256/Chrome-icon.png"
      )
    )
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val extensionLinks = if (props.appInfo.browser.exists(_.name == Browser.firefox)) {
      div(firefoxLink)
    } else if (props.appInfo.browser.exists(_.name == Browser.chrome)) {
      div(
        chromeLink
      )
    } else {
      div(
        firefoxLink,
        chromeLink
      )
    }
    div(
      mui
        .Typography()
        .component("p")(
          """
            |El propósito Cazadescuentos, ayudarte a ahorrar mientras compras por Internet:
            |""".stripMargin
        ),
      mui
        .Typography()
        .component("p")(
          """
            |- Si deseas comprar un producto, pero el precio no te convence, agrégalo a nuestra app y esta te notificara cuando baje de precio.
            |""".stripMargin
        ),
      mui
        .Typography()
        .component("p")(
          """
            |- Si acabas de comprar un producto, pagando con una tarjeta que ofrece garantía de precios, agrégalo a nuestra app y esta te notificara si baja de precio para que te puedan reembolsar la diferencia de precios.
            |""".stripMargin
        ),
      br(),
      mui
        .Typography()
        .component("p")(
          """
            |Solo visita el producto que te interesa en alguna de las tiendas soportadas, copia la URL y agregalo a cazadescuentos, la app lo seguirá por ti, notificandote cuando este baja de precio.
            |""".stripMargin
        ),
      br(),
      mui
        .Typography()
        .component("p")(
          """
            |Se recomienda instalar la app de Android, y compartir los productos que te interesan con esta app
            |""".stripMargin
        ),
      mui
        .Typography()
        .component("p")(
          """
            |Puedes ver el siguiente video para entender como:
            |""".stripMargin
        ),
      a(href := "https://play.google.com/store/apps/details?id=net.cazadescuentos.app", target := "_blank")(
        img(
          height := "40",
          src := "https://camo.githubusercontent.com/f587fa047a9b625fedbe5304fce1179255274f73/68747470733a2f2f6769746875622e6769746875626173736574732e636f6d2f696d616765732f6d6f64756c65732f64617368626f6172642f676f6f676c652d706c61792d62616467652e706e67"
        )
      ),
      br(),
      androidVideo,
      br(),
      br(),
      mui
        .Typography()
        .component("p")(
          """
            |Tambien puedes usarla en tu computadora para una mejor experiencia, mira esta
            |""".stripMargin,
          a(href := "https://www.facebook.com/105001477592284/videos/260223735388890")("demo"),
          ". Instala desde ",
          extensionLinks
        ),
      br(),
      br(),
      mui
        .Typography()
        .component("p")(
          """
            |Tiendas soportadas:
            |""".stripMargin
        ),
      div(storesComponent.map(x => div(x, br()))),
      br(),
      br(),
      br(),
      mui
        .Typography()
        .component("p")(
          """
            |Valoramos tu privacidad, y esto hacemos para cuidarla:
            |""".stripMargin
        ),
      mui
        .Typography()
        .component("p")(
          """
            |- No necesitas registrarte, no pedimos tu email o teléfono.
            |""".stripMargin
        ),
      mui
        .Typography()
        .component("p")(
          """
            |- Solo solicitamos los permisos que realmente necesitamos para que la app funcione.
            |""".stripMargin
        )
    )
  }

  private val stores = List(
    "Globales" ->
      """
        |https://www2.hm.com
        |https://m2.hm.com/m/
        |https://www.zara.com/
        |https://www.ebay.com""".stripMargin.trim.split("\n"),
    "Mexico" ->
      """
        |https://www.liverpool.com.mx
        |https://www.coppel.com
        |https://www.officedepot.com.mx
        |https://www.sanborns.com.mx
        |https://www.sams.com.mx
        |https://www.mercadolibre.com.mx
        |https://www.elektra.com.mx
        |https://www.costco.com.mx
        |https://www.homedepot.com.mx
        |https://www.walmart.com.mx
        |https://www.bestbuy.com.mx
        |https://www.elpalaciodehierro.com""".stripMargin.trim.split("\n"),
    "USA" ->
      """
        |https://www.officedepot.com
        |https://www.samsclub.com
        |https://shop.nordstrom.com
        |https://www.bestbuy.com""".stripMargin.trim.split("\n")
  )

  private val storesComponent = stores.map {
    case (tag, list) =>
      val items = list.map { item =>
        div(
          mui
            .Typography()
            .component("p")(
              " -",
              a(href := item)(item)
            )
        )()
      }
      div(
        mui
          .Typography()
          .component("p")(
            s"* $tag"
          ),
        div(items: _*)
      )
  }

  private def androidVideo = {
    val allowfullscreen = CustomAttribute[Unit]("allowFullScreen")
    val frameborder = CustomAttribute[String]("frameBorder")
    val allow = CustomAttribute[String]("allow")
    iframe(
      width := "560",
      height := "315",
      allowfullscreen := (),
      frameborder := "0",
      allow := "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture",
      src := "https://www.youtube.com/embed/42FoH_zhvyI"
    )
  }
}
