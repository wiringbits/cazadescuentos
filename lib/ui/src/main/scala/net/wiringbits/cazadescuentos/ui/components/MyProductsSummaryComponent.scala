package net.wiringbits.cazadescuentos.ui.components

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.materialUiCoreStrings.right
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.cazadescuentos.api.http.models.GetTrackedProductsResponse
import net.wiringbits.cazadescuentos.common.models.{AvailabilityStatus, OnlineStore, StoreProduct}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object MyProductsSummaryComponent {

  trait Texts {
    def noProducts: String = "No hay productos"
    def addItemsToFindDiscounts: String = "Agrega algunos productos para ayudarte a buscar descuentos"
    def store: String = "Tienda"
    def item: String = "Producto"
    def price: String = "Precio"
    def discount: String = "Descuento"
    def available: String = "Disponible"
    def initialPrice: String = "Precio inicial"
    def actions: String = "Acciones"
    def remove: String = "Quitar"
    def show: String = "Ver"
    def priceSummary(initialPrice: String, currentPrice: String) = s"De $initialPrice a $currentPrice"

  }

  trait Icons {
    def logo: String = "/images/icons/icon-96x96.png"
    def forStore(store: OnlineStore): String = s"/images/stores/${store.storeLogo}"
  }

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

  type Data = List[GetTrackedProductsResponse.TrackedProduct]
  case class Props(
      data: Data,
      delete: StoreProduct => Unit,
      texts: Texts,
      icons: Icons,
      disableMobileUI: Boolean = false
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val drawTable = props.disableMobileUI ||
      com.alexitc.materialui.facade.materialUiCore.useMediaQueryMod
        .unstableUseMediaQuery(s"(min-width:${tableMinWidth}px)")

    mui.Paper.className(classes("root"))(
      if (props.data.isEmpty) renderEmptyValues(props)
      else if (drawTable) renderNonEmptyValues(props)
      else renderNonEmptyValuesMobile(props)
    )
  }

  private def renderEmptyValues(props: Props) = {
    val classes = useStyles(())
    div(
      mui
        .Card()
        .className(classes("card"))(
          mui.CardHeader().title("Cazadescuentos"),
          mui.CardMedia(
            img(src := props.icons.logo, alt := "logo")
          ),
          mui.CardContent(
            h5(props.texts.noProducts),
            p(props.texts.addItemsToFindDiscounts)
          )
        )
    )
  }

  private def renderNonEmptyValues(props: Props) = {
    val classes = useStyles(())
    mui.Table
      .className(classes("table"))(
        mui.TableHead(
          mui.TableRow(
            mui.TableCell(props.texts.store),
            mui.TableCell.align(right)(props.texts.item),
            mui.TableCell.align(right)(props.texts.price),
            mui.TableCell.align(right)(props.texts.discount),
            mui.TableCell.align(right)(props.texts.available),
            mui.TableCell.align(right)(props.texts.initialPrice),
            mui.TableCell.align(right)(props.texts.actions)
          )
        ),
        mui.TableBody(
          props.data.map { item =>
            render(
              props,
              item,
              delete = () => props.delete(item.storeProduct),
              open = () => dom.window.open(item.url, "_blank")
            )
          }
        )
      )
  }

  private def renderNonEmptyValuesMobile(props: Props) = {
    val classes = useStyles(())
    props.data.map { item =>
      def delete(): Unit = props.delete(item.storeProduct)
      def open(): Unit = dom.window.open(item.url, "_blank")
      mui
        .Card()
        .withKey(item.url)
        .className(classes("card"))(
          mui.CardContent(
            img(
              width := "90px",
              src := props.icons.forStore(item.store),
              alt := item.store.id
            ),
            p(item.name),
            p(s"De ${item.formattedInitialPrice} a ${item.formattedLastPrice}"),
            renderDiscountCell(item, s"${props.texts.discount} "),
            div(s"${props.texts.available} ", getContentForStatusCell(item))
          ),
          mui.CardContent(
            mui
              .IconButton()
              .`aria-label`(props.texts.remove)
              .onClick(_ => delete())(
                muiIcons.Delete()
              ),
            mui
              .IconButton()
              .`aria-label`(props.texts.show)
              .onClick(_ => open())(
                muiIcons.Link()
              )
          )
        )
    }
  }

  private def render(
      props: Props,
      item: GetTrackedProductsResponse.TrackedProduct,
      delete: () => Unit,
      open: () => Unit
  ) = {
    // table-tr-dark if status is unavailable
    mui.TableRow.withKey(item.url)(
      mui.TableCell
        .set("component", "th")
        .scope("row")(
          img(
            width := "90px",
            src := props.icons.forStore(item.store),
            alt := item.store.id
          )
        ),
      mui.TableCell.align(right)(item.name),
      mui.TableCell.align(right)(item.formattedLastPrice),
      mui.TableCell.align(right)(renderDiscountCell(item)),
      mui.TableCell.align(right)(getContentForStatusCell(item)),
      mui.TableCell.align(right)(item.formattedInitialPrice),
      mui.TableCell.align(right)(
        mui
          .IconButton()
          .`aria-label`(props.texts.remove)
          .onClick(_ => delete())(
            muiIcons.Delete()
          ),
        mui
          .IconButton()
          .`aria-label`(props.texts.show)
          .onClick(_ => open())(
            muiIcons.Link()
          )
      )
    )
  }

  private def getContentForStatusCell(product: GetTrackedProductsResponse.TrackedProduct) = {
    if (product.availabilityStatus == AvailabilityStatus.Available) {
      muiIcons.CheckCircle().color(PropTypes.Color.primary)
    } else {
      muiIcons.CancelRounded().color(PropTypes.Color.secondary)
    }
  }

  private def renderDiscountCell(item: GetTrackedProductsResponse.TrackedProduct, tag: String = "") = {
    val strValue = s"$tag${item.discountPercent}%"
    if (item.lastPrice < item.initialPrice) {
      div(
        strValue,
        muiIcons.ThumbUp().color(PropTypes.Color.primary)
      )
    } else if (item.lastPrice > item.initialPrice) {
      div(
        strValue,
        muiIcons.ThumbDown().color(PropTypes.Color.secondary)
      )
    } else {
      div(strValue)
    }
  }
}
