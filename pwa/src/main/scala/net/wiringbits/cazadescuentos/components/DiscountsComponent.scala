package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.csstype.mod.FlexWrapProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{
  typographyTypographyMod,
  components => mui,
  materialUiCoreStrings => muiStrings
}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.api.http.models.GetDiscountsResponse
import net.wiringbits.cazadescuentos.common.models.OnlineStore
import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._

@react object DiscountsComponent {

  private val tableMinWidth = 950

  type MyStyles = withStylesMod.ClassNameMap[withStylesMod.ClassKeyOfStyles[withStylesMod.Styles[Theme, Unit, String]]]
  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto),
        "table" -> CSSProperties().setMinWidth(tableMinWidth),
        "card" -> CSSProperties().setMaxWidth(345),
        "container" -> CSSProperties().setDisplay("flex").setFlexWrap(FlexWrapProperty.wrap),
        "input" -> CSSProperties().setMargin(theme.spacing.unit)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  type Data = List[GetDiscountsResponse.Discount]
  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val drawTable = com.alexitc.materialui.facade.materialUiCore.useMediaQueryMod
      .unstableUseMediaQuery(s"(min-width:${tableMinWidth}px)")
    val (remoteData, setRemoteData) = Hooks.useState[Data](List.empty)
    val (filteredData, setFilteredData) = Hooks.useState[Data](List.empty)

    def onSearchUpdated(text: String): Unit = {
      val newData = if (text.isEmpty) {
        remoteData
      } else {
        remoteData.filter { item =>
          item.name.toLowerCase contains text.toLowerCase
        }
      }
      setFilteredData(newData)
    }

    val data = DiscountsDataLoader.component(
      DiscountsDataLoader.Props(
        fetch = () => props.api.productService.bestDiscounts(props.appInfo.buyerId),
        render = _ => {
          mui.Paper.className(classes("root"))(
            div(className := classes("container"))(
              mui
                .Input()
                .className(classes("input"))
                .placeholder("Buscar")
                .onChange(e => onSearchUpdated(e.target.asInstanceOf[HTMLInputElement].value.trim))
            ),
            renderDiscounts(classes, filteredData, drawTable)
          )
        },
        retryLabel = "Reintentar",
        onDataLoaded = data => {
          setRemoteData(data.data)
          setFilteredData(data.data)
        }
      )
    )

    div(
      mui
        .Typography()
        .component("p")
        .variant(typographyTypographyMod.Style.h6)(
          "Mira los mejores descuentos que hemos encontrado en la ultima semana"
        ),
      data
    )
  }

  private def renderDiscounts(classes: MyStyles, data: Data, drawTable: Boolean) = {
    mui.Paper.className(classes("root"))(
      if (drawTable) renderValues(classes, data)
      else renderValuesMobile(classes, data)
    )
  }

  private def renderValues(classes: MyStyles, data: Data) = {
    mui.Table
      .className(classes("table"))(
        mui.TableHead(
          mui.TableRow(
            mui.TableCell("#"),
            mui.TableCell("Tienda"),
            mui.TableCell.align(muiStrings.right)("Producto"),
            mui.TableCell.align(muiStrings.right)("Precio"),
            mui.TableCell.align(muiStrings.right)("Descuento"),
            mui.TableCell.align(muiStrings.right)("Precio inicial"),
            mui.TableCell.align(muiStrings.right)("Fecha"),
            mui.TableCell.align(muiStrings.right)("Acciones")
          )
        ),
        mui.TableBody(
          data.zipWithIndex.map {
            case (item, index) =>
              render(
                index + 1,
                item,
                open = () => dom.window.open(item.url, "_blank")
              )
          }
        )
      )
  }

  private def renderValuesMobile(classes: MyStyles, data: Data) = {
    data.map { item =>
      def open(): Unit = dom.window.open(item.url, "_blank")
      mui
        .Card()
        .withKey(item.id.toString)
        .className(classes("card"))(
          mui.CardContent(
            img(
              width := "90px",
              src := getStoreImagePath(item.store),
              alt := item.store.id
            ),
            p(item.name),
            p(s"De ${formattedInitialPrice(item)} a ${formattedLastPrice(item)}"),
            renderDiscountCell(item, "Descuento ")
          ),
          mui.CardContent(
            mui
              .IconButton()
              .`aria-label`("Ver")
              .onClick(_ => open())(
                muiIcons.Link()
              )
          )
        )
    }
  }

  private def render(number: Int, item: GetDiscountsResponse.Discount, open: () => Unit) = {
    // table-tr-dark if status is unavailable
    mui.TableRow.withKey(item.id.toString)(
      mui.TableCell.align(muiStrings.right)(number),
      mui.TableCell
        .set("component", "th")
        .scope("row")(
          img(
            width := "90px",
            src := getStoreImagePath(item.store),
            alt := item.store.id
          )
        ),
      mui.TableCell.align(muiStrings.right)(item.name),
      mui.TableCell.align(muiStrings.right)(formattedLastPrice(item)),
      mui.TableCell.align(muiStrings.right)(renderDiscountCell(item)),
      mui.TableCell.align(muiStrings.right)(formattedInitialPrice(item)),
      mui.TableCell.align(muiStrings.right)(formattedCreatedAt(item)),
      mui.TableCell.align(muiStrings.right)(
        mui
          .IconButton()
          .`aria-label`("Ver")
          .onClick(_ => open())(
            muiIcons.Link()
          )
      )
    )
  }

  private def getStoreImagePath(store: OnlineStore): String = {
    s"/images/stores/${store.storeLogo}"
  }

  private def renderDiscountCell(item: GetDiscountsResponse.Discount, tag: String = "") = {
    val strValue = s"$tag${item.discountPercentage}%"
    div(
      strValue,
      muiIcons.ThumbUp().color(PropTypes.Color.primary)
    )
  }

  private def formattedCreatedAt(item: GetDiscountsResponse.Discount): String = {
    try {
      java.time.ZonedDateTime
        .ofInstant(item.createdAt, java.time.ZoneId.systemDefault())
        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MMM/uuuu hh:mm a"))
    } catch {
      case _: Throwable => item.createdAt.toString
    }
  }

  private def formattedLastPrice(item: GetDiscountsResponse.Discount): String = {
    s"${item.currency.string}${item.discountPrice}"
  }

  private def formattedInitialPrice(item: GetDiscountsResponse.Discount): String = {
    s"${item.currency.string}${item.initialPrice}"
  }
}
