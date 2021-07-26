package net.wiringbits.cazadescuentos.providers

import org.scalajs.dom
import org.scalajs.dom.{Event, console}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks.useContext
import slinky.core.facade.{Hooks, React, ReactContext, ReactElement}

@react object InstallPromptProvider {
  case class Props(child: ReactElement)

  private val context = React.createContext(Option.empty[Event])

  def useInstallPrompt(): Option[Event] = useContext(context)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (event, setEvent) = Hooks.useState[Option[Event]](Option.empty)

    def handleBeforeInstallEvent(e: Event): Unit = {
      setEvent(Option(e))
    }

    def handleAppInstalled(e: Event): Unit = {
      setEvent(None)
    }

    Hooks.useEffect(() => {
      dom.window.addEventListener("beforeinstallprompt", handleBeforeInstallEvent)
      dom.window.addEventListener("appinstalled", handleAppInstalled);
    }, "")

    context.Provider(event)(props.child)
  }
}
