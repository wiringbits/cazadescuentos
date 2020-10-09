package net.cazadescuentos.facades

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSGlobalScope, JSImport}

@js.native
@JSGlobal("sidenavCzd")
object sidenavCzd extends js.Object {

  @js.native
  class customSidenav() extends js.Object {

    def apply(): Unit = js.native

    def fire(options: Options = js.native): js.Promise[Boolean] = js.native

    def destroy(): Unit = js.native
  }

  trait Options extends js.Object {
    var title: js.UndefOr[String] = js.undefined
    var text: js.UndefOr[String] = js.undefined
    var imgLogo: js.UndefOr[String] = js.undefined
    var btnCancelTxt: js.UndefOr[String] = js.undefined
    var btnAcceptTxt: js.UndefOr[String] = js.undefined
    var draggable: js.UndefOr[Boolean] = js.undefined
  }

}
