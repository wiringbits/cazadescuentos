package net.wiringbits.cazadescuentos

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.{Theme, ThemeOptions}
import com.alexitc.materialui.facade.materialUiCore.createPaletteMod.{PaletteColorOptions, PaletteOptions}
import com.alexitc.materialui.facade.materialUiCore.createTypographyMod.TypographyOptions
import com.alexitc.materialui.facade.materialUiCore.stylesMod.createMuiTheme
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement

@react object AppTheme {
  case class Props(child: ReactElement)

  val primaryColor = "#f44336"
  val typography: TypographyOptions = TypographyOptions().setUseNextVariants(true)
  val borderRadius = 8

  private val theme: Theme = createMuiTheme(
    ThemeOptions()
      .setPalette(
        PaletteOptions()
          .setPrimary(PaletteColorOptions.SimplePaletteColorOptions(primaryColor))
      )
      .setTypography(typography)
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.MuiThemeProvider(theme)(
      ThemeProvider(theme)(
        mui.CssBaseline(),
        props.child
      )
    )
  }
}
