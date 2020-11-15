package net.wiringbits.cazadescuentos.ui.components

import java.time.Instant
import java.util.UUID

import net.wiringbits.cazadescuentos.api.http.ProductHttpService
import net.wiringbits.cazadescuentos.api.http.models.GetNotificationsResponse
import net.wiringbits.cazadescuentos.ui.hooks.GenericHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import typings.csstype.csstypeStrings.auto
import typings.materialUiCore.createMuiThemeMod.Theme
import typings.materialUiCore.{components => mui}
import typings.materialUiStyles.makeStylesMod.StylesHook
import typings.materialUiStyles.mod.makeStyles
import typings.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}

import scala.concurrent.ExecutionContext.Implicits.global

@react object Notifications {

  trait Texts {
    def retry: String = "Retry"

    def notification(createdAt: Instant, message: String): String = {
      val time = try {
        java.time.ZonedDateTime
          .ofInstant(createdAt, java.time.ZoneId.systemDefault())
          .format(java.time.format.DateTimeFormatter.ofPattern("dd/MMM/uuuu hh:mm a"))
      } catch {
        case _: Throwable => createdAt.toString
      }

      s"$time: $message"
    }
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    /* If you don't need direct access to theme, this could be `StyleRules[Props, String]` */
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto),
        "card" -> CSSProperties().setMaxWidth(345)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }

  type Data = List[GetNotificationsResponse.Notification]
  case class Props(
      texts: Texts,
      buyerId: UUID,
      httpService: ProductHttpService
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (timesReloadingData, forceRefresh) = GenericHooks.useForceRefresh

    def delete(id: UUID): Unit = {
      props.httpService.notificationRead(props.buyerId, id).foreach { _ =>
        forceRefresh()
      }
    }

    mui.Paper.className(classes("root"))(
      NotificationsLoader.component(
        NotificationsLoader.Props(
          fetch = () => props.httpService.notifications(props.buyerId),
          render = response => {
            val list = response.data.headOption.map { item =>
              StickySnackbar
                .component(
                  StickySnackbar
                    .Props(
                      variant = StickySnackbar.Variant.success,
                      message = props.texts.notification(item.createdAt, item.message),
                      onClose = () => delete(item.id)
                    )
                )
                .withKey(item.id.toString)
            }.toList
            div(list)
          },
          retryLabel = props.texts.retry,
          timesReloadingData = timesReloadingData
        )
      )
    )
  }
}
