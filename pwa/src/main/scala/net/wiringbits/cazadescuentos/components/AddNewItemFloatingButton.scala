package net.wiringbits.cazadescuentos.components

import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
import com.alexitc.materialui.facade.csstype.mod.PositionProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
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
import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html.div
import typings.reactI18next.mod.useTranslation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.util.{Failure, Success}

@react object AddNewItemFloatingButton {

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "fab" -> CSSProperties()
          .setPosition(PositionProperty.fixed)
          .setWidth(64)
          .setHeight(64)
          .setBottom(88)
          .setRight("calc(50vw - 32px)"),
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  case class Props(api: API, appInfo: AppInfo, onItemAdded: () => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] {
    case Props(api, appInfo, onItemAdded) =>
      val js.Tuple3(t, _, _) = useTranslation()

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
            api.productService
              .create(appInfo.buyerId, storeProduct)
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
            setErrorMsg(t("urlErrorText").toString)
        }
      }
      def handleDialogClosed(): Unit = setDialogOpened(false)

      val classes = useStyles(())
      dialogOpened match {
        case false =>
          mui.Fab
            .className(classes("fab"))
            .`aria-label`(t("add"))
            .onClick(_ => handleAddClicked())
            .color(PropTypes.Color.primary)(
              muiIcons.Add()
            )

        case true =>
          val msg = Option(errorMsg)
            .filter(_.nonEmpty)
            .map(s => s"ERROR: $s")
            .getOrElse(t("pasteUrlText").toString)

          mui.Paper.className(classes("root"))(
            mui
              .Dialog(true)
              .onClose(_ => handleDialogClosed())(
                mui.DialogTitle(t("newProduct")),
                mui.DialogContent(msg),
                mui.TextField
                  .StandardTextFieldProps()
                  .autoFocus(true)
                  .margin(PropTypes.Margin.dense)
                  .label(t("productUrl"))
                  .onChange(e => handleItemStrUpdated(e.target.asInstanceOf[HTMLInputElement].value))
                  .fullWidth(true),
                mui.DialogActions(
                  if (addingItem) {
                    div(mui.CircularProgress())
                  } else {
                    div(mui.Button().color(PropTypes.Color.primary).onClick(_ => handleAddItem())(t("add")))
                  },
                  mui.Button().color(PropTypes.Color.secondary).onClick(_ => handleDialogClosed())(t("cancel"))
                )
              )
          )
      }
  }
}
