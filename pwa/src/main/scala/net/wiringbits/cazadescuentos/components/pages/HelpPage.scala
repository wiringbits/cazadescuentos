package net.wiringbits.cazadescuentos.components.pages

import com.alexitc.materialui.facade.csstype.mod.{
  BoxSizingProperty,
  FlexDirectionProperty,
  FlexWrapProperty,
  ObjectFitProperty,
  PositionProperty,
  TextAlignProperty
}
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.components.TutorialComponent.{globalStores, mexicoStores, usaStores}
import net.wiringbits.cazadescuentos.components.{AndroidLinkButton, SupportedStoreList}
import net.wiringbits.cazadescuentos.models.AppInfo
import net.wiringbits.cazadescuentos.ui.components.{Scaffold, ScaffoldAppBar, Subtitle, TextLine, YoutubeVideo}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.reactI18next.mod.useTranslation

import scala.scalajs.js

@react object HelpPage {
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
      "https://www.elpalaciodehierro.com"
    )

  private val usaStores = List(
    "https://www.officedepot.com",
    "https://www.samsclub.com",
    "https://shop.nordstrom.com",
    "https://www.bestbuy.com"
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val js.Tuple3(t, _, _) = useTranslation()
    val classes = useStyles(())

    Scaffold(
      appBar = Some(ScaffoldAppBar(t("help").toString)),
      child = mui.Paper(className := classes("tutorial"))(
        Subtitle(t("about").toString),
        TextLine(t("helpTextLine1").toString),
        TextLine(t("helpTextLine2").toString),
        br(),
        TextLine(t("helpTextLine3").toString),
        br(),
        TextLine(t("helpTextLine4").toString),
        br(),
        TextLine(t("helpTextLine5").toString),
        TextLine(t("helpTextLine6").toString),
        YoutubeVideo("42FoH_zhvyI"),
        AndroidLinkButton(),
        br(),
        Subtitle(t("supportedStores").toString),
        SupportedStoreList(t("globals").toString, globalStores),
        SupportedStoreList(t("mexico").toString, mexicoStores),
        SupportedStoreList(t("usa").toString, usaStores)
      )
    )
  }
}
