package net.wiringbits.cazadescuentos.ui.hooks

import com.alexitc.materialui.facade.materialUiCore.useMediaQueryMod.unstableUseMediaQuery

object MediaQueryHooks {

  def useIsLaptop(): Boolean = {
    unstableUseMediaQuery("(min-width: 769px)")
  }

  def useIsTablet(): Boolean = {
    unstableUseMediaQuery("(min-width: 426px) and (max-width: 768px)")
  }

  def useIsMobile(): Boolean = {
    unstableUseMediaQuery("(max-width: 425px)")
  }

  def useIsMobileOrTablet(): Boolean = {
    unstableUseMediaQuery("(max-width: 768px)")
  }
}
