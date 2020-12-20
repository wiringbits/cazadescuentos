package net.wiringbits.cazadescuentos.components

import net.wiringbits.cazadescuentos.API
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import com.alexitc.materialui.facade.materialUiCore.{components => mui}

@react object AdvancedComponent {

  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    div(
      mui
        .Typography()
        .component("p")(
          s"""
            |Tu id es ${props.api.storageService.load().map(_.buyerId.toString).getOrElse("")}
            |""".stripMargin
        )
    )
  }
}
