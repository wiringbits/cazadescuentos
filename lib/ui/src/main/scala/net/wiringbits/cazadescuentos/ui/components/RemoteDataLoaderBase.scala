package net.wiringbits.cazadescuentos.ui.components

import net.wiringbits.cazadescuentos.ui.models.DataState
import slinky.core.FunctionalComponent
import slinky.core.facade.{Hooks, ReactElement}
import slinky.web.html._
import typings.materialUiCore.{components => mui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait RemoteDataLoaderBase[D] {
  case class Props(fetcher: () => Future[D], render: D => ReactElement, renderTime: Int)

  val theComponent: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (dataState, setDataState) = Hooks.useState[DataState[D]](DataState.loading[D])

    def reload(): Unit = {
      setDataState(DataState.loading[D])
      props.fetcher().onComplete {
        case Success(value) => setDataState(_.loaded(value))
        case Failure(ex) => setDataState(_.failed(ex.getMessage))
      }
    }

    Hooks.useEffect(reload, List(props.renderTime))

    dataState match {
      case DataState.Loading() =>
        div(
          mui.CircularProgress()
        )

      case DataState.Loaded(data) =>
        div(
          props.render(data)
        )

      case DataState.Failed(msg) =>
        div(
          mui
            .Typography()
            .color(typings.materialUiCore.mod.PropTypes.Color.secondary)(msg),
          mui.Button.onClick(_ => reload())("Reintentar")
        )
    }
  }
}
