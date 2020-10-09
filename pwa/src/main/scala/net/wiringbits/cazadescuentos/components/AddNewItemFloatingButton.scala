package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html.div
import typings.csstype.csstypeStrings.auto
import typings.materialUiCore.createMuiThemeMod.Theme
import typings.materialUiCore.mod.PropTypes
import typings.materialUiCore.{components => mui}
import typings.materialUiIcons.{components => muiIcons}
import typings.materialUiStyles.makeStylesMod.StylesHook
import typings.materialUiStyles.mod.makeStyles
import typings.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@react object AddNewItemFloatingButton {

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "fab" -> CSSProperties().setMargin(theme.spacing.unit),
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  case class Props(api: API, onItemAdded: () => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] {
    case Props(api, onItemAdded) =>
      val (dialogOpened, setDialogOpened) = Hooks.useState(false)
      val (newItemStr, setNewItemStr) = Hooks.useState("")
      val (addingItem, setAddingItem) = Hooks.useState(false)
      val (errorMsg, setErrorMsg) = Hooks.useState("")

      def handleItemStrUpdated(newText: String): Unit = {
        setNewItemStr(newText)
        setErrorMsg("")
      }

      def handleAddClicked(): Unit = {
        setDialogOpened(true)
        setNewItemStr("")
        setErrorMsg("")
      }

      def handleAddItem(): Unit = {
        StoreProduct.parse(newItemStr) match {
          case Some(storeProduct) =>
            setAddingItem(true)
            val result = for {
              details <- api.productService.create(storeProduct)
              _ = api.storageService.add(details)
            } yield ()

            result
              .onComplete {
                case Success(_) =>
                  handleDialogClosed()
                  onItemAdded()
                  setAddingItem(false)
                case Failure(exception) =>
                  setErrorMsg(exception.getMessage)
                  setAddingItem(false)
              }

          case None =>
            setErrorMsg("La URL del producto no es correcta, o no es una tienda soportada actualmente")
        }
      }
      def handleDialogClosed(): Unit = setDialogOpened(false)

      val classes = useStyles(())
      dialogOpened match {
        case false =>
          mui.Paper.className(classes("root"))(
            mui.Fab
              .className(classes("fab"))
              .`aria-label`("Agregar")
              .onClick(_ => handleAddClicked())
              .color(PropTypes.Color.primary)(
                muiIcons.Add()
              )
          )

        case true =>
          val msg = Option(errorMsg)
            .filter(_.nonEmpty)
            .map(s => s"ERROR: $s")
            .getOrElse("Pega la URL del producto que te interesa")

          mui.Paper.className(classes("root"))(
            mui
              .Dialog(true)
              .onClose(_ => handleDialogClosed())(
                mui.DialogTitle("Nuevo producto"),
                mui.DialogContent(msg),
                mui.TextField
                  .StandardTextFieldProps()
                  .autoFocus(true)
                  .margin(PropTypes.Margin.dense)
                  .label("URL del producto")
                  .onChange(e => handleItemStrUpdated(e.target.asInstanceOf[HTMLInputElement].value))
                  .fullWidth(true),
                mui.DialogActions(
                  if (addingItem) {
                    div(mui.CircularProgress())
                  } else {
                    div(mui.Button().color(PropTypes.Color.primary).onClick(_ => handleAddItem())("Agregar"))
                  },
                  mui.Button().color(PropTypes.Color.secondary).onClick(_ => handleDialogClosed())("Cancelar")
                )
              )
          )
      }
  }
}
