package net.wiringbits.cazadescuentos.ui.components

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, ObjectFitProperty, PositionProperty}
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
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse.Discount
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object DiscountCard {
  case class Props(discount: Discount)

  private val imgSize = 72

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "discountCard" -> CSSProperties()
          .setPosition(PositionProperty.relative)
          .setDisplay("flex")
          .setMarginBottom(16)
          .setPadding("8px 16px")
          .setBorderRadius(8)
          .setOverflow("hidden"),
        "discountPercent" -> CSSProperties()
          .setPosition(PositionProperty.absolute)
          .setTop(-16)
          .setLeft(-48)
          .setWidth(128)
          .setHeight(64)
          .set("transform", "rotateZ(-45deg)")
          .setPadding("0 32px")
          .setDisplay("flex")
          .setAlignItems("flex-end")
          .setJustifyContent("center")
          .setBackgroundColor(theme.palette.primary.main),
        "discountPercentText" -> CSSProperties()
          .setColor("#fff"),
        "discountImage" -> CSSProperties()
          .setWidth(imgSize)
          .setHeight("auto")
          .setBorderRadius(8)
          .setObjectFit(ObjectFitProperty.`scale-down`),
        "discountBody" -> CSSProperties()
          .setFlex("auto")
          .setDisplay("flex")
          .setMarginLeft(16)
          .setFlexDirection(FlexDirectionProperty.column),
        "discountRow" -> CSSProperties()
          .setDisplay("flex")
          .setMargin("8px 0"),
        "discountColumn" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column),
        "discountName" -> CSSProperties()
          .setFlex(1)
          .setFontSize("1em"),
        "discountRowPrice" -> CSSProperties()
          .setDisplay("flex")
          .setMargin("8px 0")
          .setAlignItems("center")
          .setJustifyContent("space-between"),
        "discountPriceContainer" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("baseline")
          .setJustifyContent("flex-end"),
        "discountPriceCaption" -> CSSProperties()
          .setMarginRight(4),
        "discountLastPrice" -> CSSProperties()
          .setColor("#4caf50")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    mui
      .Paper(className := classes("discountCard"))
      .elevation(1)(
        div(className := classes("discountPercent"))(
          mui
            .Typography(className := classes("discountPercentText"))(s"${props.discount.discountPercentage}%")
            .variant(muiStrings.h6)
        ),
        img(
          className := classes("discountImage"),
          src := s"/images/stores/${props.discount.store.storeLogo}",
          alt := props.discount.store.id
        ),
        div(className := classes("discountBody"))(
          div(className := classes("discountRow"))(
            mui.Typography(className := classes("discountName"))(props.discount.name).variant(muiStrings.subtitle2)
          ),
          mui.Divider(),
          div(className := classes("discountRowPrice"))(
            div(className := classes("discountRow"))(
              mui
                .IconButton()
                .onClick(_ => {})(muiIcons.Favorite()),
              mui
                .IconButton()
                .onClick(_ => {})(muiIcons.Share()),
              mui
                .IconButton()
                .onClick(_ => dom.window.open(props.discount.url, "_blank"))(muiIcons.OpenInNew())
            ),
            div(className := classes("discountColumn"))(
              div(className := classes("discountPriceContainer"))(
                mui.Typography(className := classes("discountPriceCaption"))("De").variant(muiStrings.caption),
                mui.Typography()(formattedInitialPrice(props.discount)).variant(muiStrings.body1)
              ),
              div(className := classes("discountPriceContainer"))(
                mui.Typography(className := classes("discountPriceCaption"))("A").variant(muiStrings.caption),
                mui
                  .Typography(className := classes("discountLastPrice"))(formattedLastPrice(props.discount))
                  .variant(muiStrings.h5)
              )
            )
          )
        )
      )
  }

  private def formattedLastPrice(item: Discount): String = {
    s"${item.currency.string}${item.discountPrice}"
  }

  private def formattedInitialPrice(item: Discount): String = {
    s"${item.currency.string}${item.initialPrice}"
  }
}
