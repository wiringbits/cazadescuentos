package net.wiringbits.cazadescuentos.ui.components

import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
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

/** A snackbar that isn't closed automatically
  */
@react object StickySnackbar {

  sealed trait Variant

  object Variant {
    final case object success extends Variant
    final case object info extends Variant
    final case object warning extends Variant
    final case object error extends Variant
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "icon" -> CSSProperties().setFontSize(26),
        "message" -> CSSProperties().setDisplay("flex").setAlignItems("center"),
        "success" -> CSSProperties().setBackgroundColor("#43a047"), // green[600] from mui docs
        "warning" -> CSSProperties().setBackgroundColor("#ffa000"), // amber[700] from mui docs
        "error" -> CSSProperties().setBackgroundColor(theme.palette.error.dark),
        "info" -> CSSProperties().setBackgroundColor(theme.palette.primary.dark)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  case class Props(variant: Variant, message: String, labelClose: String = "Close", onClose: () => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    mui
      .SnackbarContent()
      .className(classes(props.variant.toString))
      .message(
        span(className := classes("message"), props.message)
      )
      .action(
        mui
          .IconButton()
          .className(classes("icon"))
          .`aria-label`(props.labelClose)
          .color(PropTypes.Color.inherit)
          .onClick(_ => props.onClose())(
            muiIcons.Close()
          )
      )
  }
}
