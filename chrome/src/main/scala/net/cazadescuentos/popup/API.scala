package net.cazadescuentos.popup

import net.cazadescuentos.background.BackgroundAPI
import net.cazadescuentos.common.I18NMessages
import net.wiringbits.cazadescuentos.api.http.ProductHttpService

case class API(backgroundAPI: BackgroundAPI, productHttpService: ProductHttpService, messages: I18NMessages)
