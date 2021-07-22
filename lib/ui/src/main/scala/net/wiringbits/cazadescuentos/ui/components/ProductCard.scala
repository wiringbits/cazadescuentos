package net.wiringbits.cazadescuentos.ui.components

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
import net.wiringbits.cazadescuentos.api.http.models.GetTrackedProductsResponse.TrackedProduct
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html._

@react object ProductCard {
  case class Props(product: TrackedProduct, onDelete: () => Unit)

  private val imgSize = 80

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "productCard" -> CSSProperties()
          .setMargin("16px 0")
          .setBorderRadius(8)
          .setOverflow("hidden")
          .setDisplay("flex"),
        "image" -> CSSProperties()
          .setMarginLeft(8)
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("center")
          .set(
            "& img",
            CSSProperties()
              .setWidth(imgSize)
              .setHeight(imgSize)
              .setObjectFit(ObjectFitProperty.contain)
          ),
        "body" -> CSSProperties()
          .setWidth("100%")
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setPadding(16),
        "title" -> CSSProperties()
          .setMarginBottom(8),
        "actions" -> CSSProperties(),
        "info" -> CSSProperties()
          .setMarginTop(8)
          .setDisplay("flex")
          .setJustifyContent("space-between"),
        "price" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column),
        "priceItem" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("baseline")
          .setJustifyContent("flex-end")
          .set(
            "& span",
            CSSProperties()
              .setMarginRight(4)
              .setFontSize("0.9em")
          ),
        "initialPrice" -> CSSProperties()
          .setTextDecoration("line-through"),
        "lastPrice" -> CSSProperties()
          .setColor("#4caf50")
          .setFontSize("1.2em")
          .setFontWeight(600)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val imgSrc = s"/images/stores/${props.product.store.storeLogo}"
    val image = div(className := classes("image"))(
      img(src := imgSrc, alt := props.product.store.id)
    )

    val discount = if (props.product.hasDiscount) {
      Fragment(mui.Typography(props.product.formattedDiscountPercent))
    } else {
      Fragment()
    }

    val body = div(className := classes("body"))(
      mui
        .Typography(props.product.name)
        .variant(muiStrings.subtitle2)
        .className(classes("title")),
      mui.Divider(),
      div(className := classes("info"))(
        discount,
        div(className := classes("actions"))(
          mui
            .IconButton(muiIcons.Delete())
            .onClick(_ => props.onDelete()),
          mui
            .IconButton(muiIcons.OpenInNew())
            .onClick(_ => dom.window.open(props.product.url, "_blank"))
        ),
        div(className := classes("price"))(
          div(className := classes("priceItem"))(
            mui.Typography("De").component("span"),
            mui.Typography(props.product.formattedInitialPrice).className(classes("initialPrice"))
          ),
          div(className := classes("priceItem"))(
            mui.Typography("A").component("span"),
            mui.Typography(props.product.formattedLastPrice).className(classes("lastPrice"))
          )
        )
      )
    )

    mui.Paper(className := classes("productCard"))(
      image,
      body
    )
  }
}
