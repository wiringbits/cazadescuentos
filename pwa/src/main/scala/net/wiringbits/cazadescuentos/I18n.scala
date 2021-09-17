package net.wiringbits.cazadescuentos

import org.scalablytyped.runtime.StringDictionary
import typings.i18next.i18nextBooleans.`false`
import typings.i18next.mod.{InitOptions, InterpolationOptions, TFunction, default => i18n}
import typings.i18nextBrowserLanguagedetector.mod.{default => LanguageDetector}
import typings.reactI18next.mod.initReactI18next

import scala.scalajs.js
import scala.scalajs.js.Promise

object I18n {

  val namespace = "translations"

  val en = "en"
  private val enTexts = StringDictionary[js.Any](
    "home" -> "Home",
    "discounts" -> "Discounts",
    "help" -> "Help",
    "about" -> "About",
    "supportedStores" -> "Supported stores",
    "search" -> "Search",
    "install" -> "Install",
    "installCardDescription" -> "For a better experience, install the app on your computer from https://cazadestamos.net",
    "helpTextLine1" -> "The purpose of Cazadescuentos is to help you save while shopping online:",
    "helpTextLine2" -> "If you want to buy a product, but the price does not convince you, add it to our app and it will notify you when the price drops.",
    "helpTextLine3" -> "If you have just bought a product, paying with a card that offers a price guarantee, add it to our app and it will notify you if the price drops so that you can be reimbursed for the price difference.",
    "helpTextLine4" -> "Just visit the product you are interested in in one of the supported stores, copy the URL and add it to a Cazadescuentos, the app will follow it for you, notifying you when the price drops.",
    "helpTextLine5" -> "It is recommended to install the Android app, and share the products that interest you with this app.",
    "helpTextLine6" -> "You can watch the following video to understand how:",
    "globals" -> "Globals",
    "mexico" -> "Mexico",
    "usa" -> "USA",
    "newProduct" -> "New product",
    "add" -> "Add",
    "cancel" -> "Cancel",
    "productUrl" -> "Product URL",
    "pasteUrlText" -> "Paste the URL of the product you are interested in",
    "urlErrorText" -> "The product URL is not correct, or it is not a currently supported store"
  )

  val es = "es"
  private val esTexts = StringDictionary[js.Any](
    "home" -> "Inicio",
    "discounts" -> "Descuentos",
    "help" -> "Ayuda",
    "about" -> "Acerca de",
    "supportedStores" -> "Tiendas soportadas",
    "search" -> "Buscar",
    "install" -> "Instalar",
    "installCardDescription" -> "Para una mejor experiencia, instala la app en tu computadora desde https://cazadescuentos.net",
    "helpTextLine1" -> "El propósito de Cazadescuentos, es ayudarte a ahorrar mientras compras por Internet:",
    "helpTextLine2" -> "Si deseas comprar un producto, pero el precio no te convence, agrégalo a nuestra app y esta te notificara cuando baje de precio.",
    "helpTextLine3" -> "Si acabas de comprar un producto, pagando con una tarjeta que ofrece garantía de precios, agrégalo a nuestra app y esta te notificara si baja de precio para que te puedan reembolsar la diferencia de precios.",
    "helpTextLine4" -> "Solo visita el producto que te interesa en alguna de las tiendas soportadas, copia la URL y agregalo a cazadescuentos, la app lo seguirá por ti, notificandote cuando este baja de precio.",
    "helpTextLine5" -> "Se recomienda instalar la app de Android, y compartir los productos que te interesan con esta app.",
    "helpTextLine6" -> "Puedes ver el siguiente vídeo para entender cómo:",
    "globals" -> "Globales",
    "mexico" -> "México",
    "usa" -> "USA",
    "newProduct" -> "Nuevo producto",
    "add" -> "Agregar",
    "cancel" -> "Cancelar",
    "productUrl" -> "URL del producto",
    "pasteUrlText" -> "Pega la URL del producto que te interesa",
    "urlErrorText" -> "La URL del producto no es correcta, o no es una tienda soportada actualmente"
  )

  def initialize(): Promise[TFunction] =
    i18n
      .use(new LanguageDetector)
      .use(initReactI18next)
      .init(
        InitOptions()
          .setResources(
            StringDictionary(
              en -> StringDictionary(namespace -> enTexts),
              es -> StringDictionary(namespace -> esTexts)
            )
          )
          .setFallbackLng(en)
          .setDebug(true)
          .setDefaultNS(namespace)
          .setKeySeparator(`false`)
          .setInterpolation(InterpolationOptions().setEscapeValue(false))
      )
}
