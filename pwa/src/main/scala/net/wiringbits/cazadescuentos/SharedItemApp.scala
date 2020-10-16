package net.wiringbits.cazadescuentos

import net.wiringbits.cazadescuentos.common.models.StoreProduct
import net.wiringbits.cazadescuentos.models.AppInfo
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html.div
import typings.materialUiCore.{components => mui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * This entrypoint is used specifically when an url is being shared directly from the browser
 */
@react object SharedItemApp {

  sealed trait State

  object State {
    final case object InProgress extends State
    final case object Done extends State
    final case class Failed(error: String) extends State
  }

  case class Props(api: API, appInfo: AppInfo)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (state, setState) = Hooks.useState[State](State.InProgress)

    Hooks.useEffect(
      () => {
        StoreProduct.parse(props.appInfo.sharedUrl.getOrElse("")) match {
          case Some(value) =>
            val result = for {
              details <- props.api.productService.create(value)
              _ = props.api.storageService.add(details)
            } yield ()

            result.onComplete {
              case Success(_) => setState(State.Done)
              case Failure(exception) => setState(State.Failed(exception.getMessage))
            }

          case None => setState(State.Failed("La url compartida no corresponde a un producto, intenta con otra"))
        }
      },
      ""
    )

    state match {
      case State.InProgress => div(mui.CircularProgress())
      case State.Done =>
        div(
          App.component(App.Props(props.api, props.appInfo))
        )

      case State.Failed(error) =>
        div(
          mui
            .Dialog(true)
            .onClose(_ => setState(State.Done))(
              mui.DialogTitle("Error"),
              mui.DialogContent(error)
            )
        )
    }
  }
}
