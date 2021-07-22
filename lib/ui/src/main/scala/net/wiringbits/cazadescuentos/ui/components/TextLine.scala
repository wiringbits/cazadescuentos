package net.wiringbits.cazadescuentos.ui.components

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import slinky.core._
import slinky.core.annotations._

@react object TextLine {
  case class Props(text: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.Typography()(props.text)
  }
}
