package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.api.storage.models.StoredProduct
import net.wiringbits.cazadescuentos.common.models.OnlineStore
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.csstype.csstypeStrings.auto
import typings.materialUiCore.createMuiThemeMod.Theme
import typings.materialUiCore.materialUiCoreStrings.right
import typings.materialUiCore.mod.PropTypes
import typings.materialUiCore.{components => mui}
import typings.materialUiIcons.{components => muiIcons}
import typings.materialUiStyles.makeStylesMod.StylesHook
import typings.materialUiStyles.mod.makeStyles
import typings.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}

@react object MyProductsSummaryComponent {

  private val tableMinWidth = 950

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto),
        "table" -> CSSProperties().setMinWidth(tableMinWidth),
        "card" -> CSSProperties().setMaxWidth(345)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  type Data = List[StoredProduct]
  case class Props(data: Data, delete: StoredProduct => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val drawTable = typings.materialUiCore.useMediaQueryMod.unstableUseMediaQuery(s"(min-width:${tableMinWidth}px)")
    mui.Paper.className(classes("root"))(
      if (props.data.isEmpty) renderEmptyValues
      else if (drawTable) renderNonEmptyValues(props)
      else renderNonEmptyValuesMobile(props)
    )
  }

  private def renderEmptyValues = {
    val classes = useStyles(())
    div(
      mui
        .Card()
        .className(classes("card"))(
          mui.CardHeader().title("Cazadescuentos"),
          mui.CardMedia(
            img(src := "/images/icons/icon-96x96.png", alt := "logo")
          ),
          mui.CardContent(
            h5("No hay productos"),
            p("Agrega algunos productos para ayudarte a buscar descuentos")
          )
        )
    )
  }

  private def renderNonEmptyValues(props: Props) = {
    val classes = useStyles(())
    mui.Table
      .className(classes("table"))(
        // thead-dark
        mui.TableHead(
          mui.TableRow(
            mui.TableCell("Tienda"),
            mui.TableCell.align(right)("Producto"),
            mui.TableCell.align(right)("Precio"),
            mui.TableCell.align(right)("Descuento"),
            mui.TableCell.align(right)("Disponible"),
            mui.TableCell.align(right)("Precio inicial"),
            mui.TableCell.align(right)("Acciones")
          )
        ),
        mui.TableBody(
          props.data.map { item =>
            render(item, delete = () => props.delete(item), open = () => dom.window.open(item.sourceUrl, "_blank"))
          }
        )
      )
  }

  private def renderNonEmptyValuesMobile(props: Props) = {
    val classes = useStyles(())
    props.data.map { item =>
      def delete(): Unit = props.delete(item)
      def open(): Unit = dom.window.open(item.sourceUrl, "_blank")
      mui
        .Card()
        .withKey(item.storeProduct.url)
        .className(classes("card"))(
          mui.CardContent(
            img(
              width := "90px",
              src := getStoreImagePath(item.store),
              alt := item.store.id
            ),
            p(item.name),
            p(s"De ${item.formattedOriginalPrice} a ${item.formattedPrice}"),
            renderDiscountCell(item, "Descuento "),
            div("Disponible ", getContentForStatusCell(item))
          ),
          mui.CardContent(
            mui
              .IconButton()
              .`aria-label`("Quitar")
              .onClick(_ => delete())(
                muiIcons.Delete()
              ),
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

  private def render(item: StoredProduct, delete: () => Unit, open: () => Unit) = {
    // table-tr-dark if status is unavailable
    mui.TableRow.withKey(item.storeProduct.url)(
      mui.TableCell
        .set("component", "th")
        .scope("row")(
          img(
            width := "90px",
            src := getStoreImagePath(item.store),
            alt := item.store.id
          )
        ),
      mui.TableCell.align(right)(item.name),
      mui.TableCell.align(right)(item.formattedPrice),
      mui.TableCell.align(right)(renderDiscountCell(item)),
      mui.TableCell.align(right)(getContentForStatusCell(item)),
      mui.TableCell.align(right)(item.formattedOriginalPrice),
      mui.TableCell.align(right)(
        mui
          .IconButton()
          .`aria-label`("Quitar")
          .onClick(_ => delete())(
            muiIcons.Delete()
          ),
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

  private def getContentForStatusCell(product: StoredProduct) = {
    if (product.status == StoredProduct.Status.Available) {
      muiIcons.CheckCircle().color(PropTypes.Color.primary)
    } else {
      muiIcons.CancelRounded().color(PropTypes.Color.secondary)
    }
  }

  private def renderDiscountCell(item: StoredProduct, tag: String = "") = {
    val strValue = s"${tag}${item.discountPercent}%"
    if (item.price < item.originalPrice) {
      div(
        strValue,
        muiIcons.ThumbUp().color(PropTypes.Color.primary)
      )
    } else if (item.price > item.originalPrice) {
      div(
        strValue,
        muiIcons.ThumbDown().color(PropTypes.Color.secondary)
      )
    } else {
      div(strValue)
    }
  }
}
