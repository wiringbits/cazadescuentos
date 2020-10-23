package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.models.AppInfo
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._
import slinky.core._
import typings.materialUiCore.{typographyTypographyMod => muiTypography}
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

@react object TutorialComponent {

  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    div(
      mui
        .Typography()
        .component("p")(
          """
            |Se recomienda instalar la app de Android, y compartir los productos que te interesan con esta app
            |""".stripMargin
        ),
      mui
        .Typography()
        .component("p")(
          """
            |Puedes ver el siguiente video para entender como:
            |""".stripMargin
        ),
      a(href := "https://play.google.com/store/apps/details?id=net.cazadescuentos.app", target := "_blank")(
        img(
          height := "40",
          src := "https://camo.githubusercontent.com/f587fa047a9b625fedbe5304fce1179255274f73/68747470733a2f2f6769746875622e6769746875626173736574732e636f6d2f696d616765732f6d6f64756c65732f64617368626f6172642f676f6f676c652d706c61792d62616467652e706e67"
        )
      ),
      br(),
      androidVideo
    )
  }

  private def androidVideo = {
    val allowfullscreen = CustomAttribute[Unit]("allowFullScreen")
    val frameborder = CustomAttribute[String]("frameBorder")
    val allow = CustomAttribute[String]("allow")
    iframe(
      width := "560",
      height := "315",
      allowfullscreen := (),
      frameborder := "0",
      allow := "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture",
      src := "https://www.youtube.com/embed/42FoH_zhvyI"
    )
  }
}
