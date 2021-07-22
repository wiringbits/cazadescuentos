package net.wiringbits.cazadescuentos.ui.components

import com.alexitc.materialui.facade.csstype.mod._
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import slinky.core._
import slinky.web.html._
import slinky.core.annotations._
import slinky.core.facade._

@react object YoutubeVideo {
  case class Props(id: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "youtubeVideo" -> CSSProperties()
          .setWidth("100%")
          .setMinHeight(340)
          .setBorderRadius("16px")
          .setMargin("16px 0")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val allowfullscreen = CustomAttribute[Unit]("allowFullScreen")
    val frameborder = CustomAttribute[String]("frameBorder")
    val allow = CustomAttribute[String]("allow")

    iframe(
      className := classes("youtubeVideo"),
      allowfullscreen := (),
      frameborder := "0",
      allow := "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture",
      src := s"https://www.youtube.com/embed/${props.id}"
    )
  }
}
