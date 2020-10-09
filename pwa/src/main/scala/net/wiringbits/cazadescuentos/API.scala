package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.common.http.ProductHttpService
import net.wiringbits.cazadescuentos.common.services.ProductUpdaterService
import net.wiringbits.cazadescuentos.common.storage.StorageService

case class API(
    productService: ProductHttpService,
    storageService: StorageService,
    productUpdaterService: ProductUpdaterService
)
