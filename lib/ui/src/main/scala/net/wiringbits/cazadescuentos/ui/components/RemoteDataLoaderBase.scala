package net.wiringbits.cazadescuentos.ui.components

import net.wiringbits.cazadescuentos.ui.models.DataState
import slinky.core.FunctionalComponent
import slinky.core.facade.{Hooks, ReactElement}
import slinky.web.html._
import typings.materialUiCore.{components => mui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * A reusable component to render some data that's retrieved from a remote source, providing:
 * - A progress indicator when the data is being retrieved.
 * - Invoking the render function when the data is available, to render such data.
 * - Displaying an error message when retrieving the data has failed, as well as displaying a
 *   retry button so that the user is able to try again.
 *
 * @tparam D The data to fetch and render
 */
trait RemoteDataLoaderBase[D] {

  /**
   * TODO: timesReloadingData is a hacky way to force reloading the data, there should be a better way
   *
   * @param fetch the function to fetch the data
   * @param render the function to render the data once it is available
   * @param retryLabel the label to use in the button that retries the operation
   * @param timesReloadingData a helper to force reloading the data, useful when the parent component,
   *                           or rendered component needs to reload the data.
   */
  case class Props(
      fetch: () => Future[D],
      render: D => ReactElement,
      retryLabel: String = "Retry",
      timesReloadingData: Int = 0
  )

  val theComponent: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (dataState, setDataState) = Hooks.useState[DataState[D]](DataState.loading[D])

    def reload(): Unit = {
      def f(state: DataState[D]): DataState[D] = state match {
        case _: DataState.Failed[D] => DataState.loading[D]
        case _ =>
          // avoid mutating the state to avoid the flickering effect
          state
      }
      setDataState(f _)
      props.fetch().onComplete {
        case Success(value) => setDataState(_.loaded(value))
        case Failure(ex) => setDataState(_.failed(ex.getMessage))
      }
    }

    Hooks.useEffect(reload, List(props.timesReloadingData))

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
          mui.Button.onClick(_ => reload())(props.retryLabel)
        )
    }
  }
}
