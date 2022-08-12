package net.wiringbits.cazadescuentos.common.models

import scala.util.matching.Regex

sealed abstract class OnlineStore(
    val id: String,
    val name: String,
    val baseUrl: String,
    val storeLogo: String,
    productRegEx: Regex
) {
  override def toString: String = name

  def url(productId: String): String = baseUrl + productId

  // returns true if the given productId seems to belong to this store
  def looksLikeProduct(productId: String): Boolean = {
    productRegEx.pattern.matcher(productId).matches()
  }
}

object OnlineStore {
  final case object NintendoEshop
      extends OnlineStore(
        "nintendoEshop",
        "Nintendo Eshop",
        "https://www.nintendo.com/",
        "nintendoEshop.png",
        raw"(\w{2}\-\w{2}\/)?store\/products\/[\w\W\-]+".r
        // should match:
        // "es-mx/store/products/portal-companion-collection-switch"
        // "store/products/portal-companion-collection-switch"
      )

  final case object Liverpool
      extends OnlineStore(
        "liverpool",
        "Liverpool",
        "https://www.liverpool.com.mx/tienda/pdp/",
        "liverpool.png",
        raw"[\w\W\-]+".r // should match "crackdown-3-xbox-one/1039886789", TODO: Improve regex
      )

  final case object HandM
      extends OnlineStore(
        "hm",
        "H&M",
        "https://www2.hm.com/",
        "Hm.png",
        raw"[\w\W]+/productpage[\w\W]+".r // should match "es_mx/productpage.0700802002.html", TODO: Improve regex
      )

  final case object HandMMobile
      extends OnlineStore(
        "hmMobile",
        "H&M",
        "https://m2.hm.com/m/",
        "Hm.png",
        raw"[\w\W]+/productpage[\w\W]+".r // should match "es_mx/productpage.0700802002.html", TODO: Improve regex
      )

  final case object Zara
      extends OnlineStore(
        "zara",
        "Zara",
        "https://www.zara.com/",
        "zara.jpg",
        raw"\w{2}/\w{2}/[\w\W]+p[\d]+\.html.*$$".r
        // should match the following:
        // "mx/es/polo-punto-p09240403.html"
        // "mx/es/cazadora-t-cnica-capucha-p08574470.html?v1=148816049&v2=2110795"
        // "es/es/corbata-estrecha-otomán-p07347390.html?v1=17909490&v2=1388752"
        //
        // tests @ cazadescuentos/server-scala/src/test/scala/common/OnlineStoreTest.scala
        // testOnly net.cazadescuentos.common.OnlineStoreTest
      )

  final case object Coppel
      extends OnlineStore(
        "coppel",
        "Coppel",
        "https://www.coppel.com/",
        "coppel.png",
        raw"[\w\W/\/\-]+-(pr|pm)-[\w\W]+".r // should match "refrigerador-mabe-top-mount-rme1436xmxe-de-14-pies-pm-6168343", TODO: Improve regex
      )

  final case object Ebay
      extends OnlineStore(
        "ebay",
        "Ebay",
        "https://www.ebay.com/itm/",
        "ebay.jpg",
        raw"[\w\W\/]+".r // should match "BRAND-NEW-Nintendo-Switch-Lite-32-GB-Gray-Handheld-Console-Grey/293292906825", TODO: Improve regex
      )

  final case object OfficeDepotMx
      extends OnlineStore(
        "officeDepotMx",
        "OfficeDepot",
        "https://www.officedepot.com.mx/",
        "officeDepot.png",
        raw"[\w\W]+(\/p\/\d+)$$".r // should match "officedepot/en/Categoría/Todas/Papel/Papel-Especializado/Papel-Forma-Continua/PAPEL-FORMA-CONTINUA-PCM-(1000-HJS-%2C-3-TANTOS%2C-BLANCO)/p/40302", TODO: Improve regex
      )

  final case object OfficeDepotUs
      extends OnlineStore(
        "officeDepotUs",
        "OfficeDepot",
        "https://www.officedepot.com/",
        "officeDepot.png",
        raw"[\w\W]*\/products\/\d+\/[\w\W]*".r // should match "officedepot/en/Categoría/Todas/Papel/Papel-Especializado/Papel-Forma-Continua/PAPEL-FORMA-CONTINUA-PCM-(1000-HJS-%2C-3-TANTOS%2C-BLANCO)/p/40302", TODO: Improve regex
      )

  final case object Sanborns
      extends OnlineStore(
        "sanborns",
        "Sanborns",
        "https://www.sanborns.com.mx/",
        "sanborns.jpg",
        raw"[\w\W]*producto\/\d+\/[\w\W]*".r // should match "producto/69877/paquete-2-peliculas-bluray-spider-man/", TODO: Improve regex
      )

  final case object SamsClubMx
      extends OnlineStore(
        "samsClubMx",
        "SamsClub",
        "https://www.sams.com.mx/",
        "samsClub.png",
        raw"[\w\W]+(\/\d+)$$".r // should match "pantallas/pantalla-jvc-50-pulgadas-4k-led-smart-tv/980011980", TODO: Improve regex
      )

  final case object SamsClubUs
      extends OnlineStore(
        "samsClubUs",
        "SamsClub",
        "https://www.samsclub.com/",
        "samsClub.png",
        raw"p\/[\w\W]*prod\d+[\w\W]*".r // should match "p/lg-55in-class-7300-series-4k-ultra-hd-smart-hdr-tv-thinq- 55um7300aue/prod23101194?xid=plp_product_1_1", TODO: Improve regex
      )

  final case object MercadoLibreMx
      extends OnlineStore(
        "mercadolibreMx",
        "Mercado Libre",
        "https://www.mercadolibre.com.mx/",
        "mercadoLibre.png",
        raw"[\w\W]*\/p\/MLM[\w\W]+".r // should match "MLM14141084"
      )

  final case object MercadoLibreMxArticulos
      extends OnlineStore(
        "mercadolibreMxArticulos",
        "Mercado Libre",
        "https://articulo.mercadolibre.com.mx/",
        "mercadoLibre.png",
        raw"MLM[\w\W]+".r // should match "MLM-662986971-tenis-de-nina-de-luces-frozen-little-kids-del-13-al-215-_JM"
      )

  final case object Nordstrom
      extends OnlineStore(
        "nordstrom",
        "Nordstrom",
        "https://shop.nordstrom.com/s/",
        "nordstrom.jpg",
        raw"[\w\W]+origin=[\w\W]+".r // should match "treasure-bond-colorblock-sweater-big-girls/5323861?origin=category-personalizedsort&breadcrumb=Home%2FKids%2FGirls&color=pink adobe- navy multi", TODO: Improve regex
      )

  final case object Elektra
      extends OnlineStore(
        "elektra",
        "Elektra",
        "https://www.elektra.com.mx/",
        "Elektra.jpg",
        raw"[\w\W]+(\/p)$$".r // should match "motocicleta-de-trabajo-italika-ft150g-negro-34002533/p", TODO: Improve regex
      )

  final case object CostcoMx
      extends OnlineStore(
        "costcoMx",
        "Costco",
        "https://www.costco.com.mx/",
        "costcoMx.png",
        raw"[\w\W]+(\/p\/\w*\d+.*)$$".r // should match "Linea-Blanca/Refrigeradores-y-Congeladores/Congeladores/Refrigerdador-KitchenAid-bajo-el-mostrador-de-24-acero-inoxidable-negro/p/656082", potentially "656082-2", TODO: Improve regex
      )

  final case object HomeDepotMx
      extends OnlineStore(
        "homeDepotMx",
        "homeDepot",
        "https://www.homedepot.com.mx/",
        "homeDepot.png",
        raw"[\w\W]+((-|\/)\d{5,})($$|\?[\w\W]+)".r // should match "linea-blanca-y-cocinas/lavadoras-y-secadoras/lavadoras/lavadora-mabe-22-kg-alta-eficiencia-13-ciclos-122199?storeId=10351&storeId=10351&catalogId=10101&langId=-5&krypto=%2FwbHqPIPO4DS%2B2ceUHs3D2V3egal%2FUeSnX6UG6Bp9klZVYFmY1ixinix%2FZJ9t8OJSZCM5EomBVRmoUQk0mIPk5%2F72C03fi2fDwMDnFMyGCqj6ICkk7RWaRSqceTRmU6CZ%2FWU5mz3xezTNMb71%2FOniTUxk4oqq8Em%2B9a2c7wZG%2BXbnn51ZN%2BhWTLaxLMsm81b&ddkey=https%3AClickInfo", TODO: Improve regex
      )

  final case object HomeDepotMxHttp
      extends OnlineStore(
        "homeDepotMxHttp",
        "homeDepot",
        "http://www.homedepot.com.mx/",
        "homeDepot.png",
        raw"[\w\W]+((-|\/)\d{5,})($$|\?[\w\W]+)".r // should match "navidad/decoracion-exterior/inflables-y-exterior/inflable-iluminado-navideno-santa-claus-y-arbol-213-m-138331", TODO: Improve regex
      )

  final case object WalmartMx
      extends OnlineStore(
        "walmartMx",
        "Walmart",
        "https://www.walmart.com.mx/",
        "walmart.png",
        raw"[\w\W]+(_\d+)$$".r // should match "temporada/halloween/articulos-decorativos-halloween/mascara-de-alien-area-51-rev_00750109156513", TODO: Improve regex
      )

  final case object WalmartMxSuper
      extends OnlineStore(
        "walmartMxSuper",
        "Walmart",
        "https://super.walmart.com.mx/",
        "walmart.png",
        raw"[\w\W]+(\/\d+(\?[\w\W]+)?)$$".r // should match "cafe-te-y-sustitutos/cafe-soluble-nescafe-clasico-300-g/00750105862463", TODO: Improve regex
      )

  final case object BestbuyMx
      extends OnlineStore(
        "bestbuyMx",
        "bestbuy",
        "https://www.bestbuy.com.mx/",
        "bestbuy.png",
        raw"(p\/)[\w\W]+(\/\d+)$$".r // should match "mabe-refrigerador-15-superior-despachador-de-agua-rmt400rymre0-plata/1000217474", TODO: Improve regex
      )

  final case object BestbuyUs
      extends OnlineStore(
        "bestbuyUs",
        "bestbuy",
        "https://www.bestbuy.com/",
        "bestbuy.png",
        raw"(site\/)[\w\W]+(\/\d+.p\?)(skuId=\d+(&intl=nosplash)?)$$".r // should match "site/apple-ipad-latest-model-with-wi-fi-32gb-space-gray/5985609.p?skuId=5985609&intl=nosplash", TODO: Improve regex
      )

  final case object PalacioDeHierro
      extends OnlineStore(
        "palacioDeHierro",
        "Palacio De Hierro",
        "https://www.elpalaciodehierro.com/",
        "palacioDeHierro.png",
        raw"[\w\W]+(-\d+.html)$$".r // should match "sony-pantalla-43-led-android-x-pro-4k-40088123.html", TODO: Improve regex
      )

  final case object AmazonMx
      extends OnlineStore(
        "amazonMx",
        "Amazon Mx",
        "https://www.amazon.com.mx/",
        "amazonMx.png",
        raw".+".r // should match "YAYAS-Bota-Lluvia-Marino-25/dp/B07F92LF7F?th=1&psc=1", TODO: Improve regex
      )

  val available: List[OnlineStore] = List(
    NintendoEshop,
    Liverpool,
    HandM,
    HandMMobile,
    Zara,
    Coppel,
    Ebay,
    OfficeDepotMx,
    OfficeDepotUs,
    Sanborns,
    SamsClubMx,
    SamsClubUs,
    MercadoLibreMx,
    MercadoLibreMxArticulos,
    Nordstrom,
    Elektra,
    CostcoMx,
    HomeDepotMx,
    HomeDepotMxHttp,
    WalmartMx,
    WalmartMxSuper,
    BestbuyUs,
    PalacioDeHierro,
    AmazonMx
  )

  def from(string: String): Option[OnlineStore] = {
    OnlineStore.available
      .find(_.id equalsIgnoreCase string)
  }
}
