package org.jetbrains.sbt
package project

import com.intellij.notification._
import com.intellij.openapi.externalSystem.model.task.{ExternalSystemTaskId, ExternalSystemTaskNotificationListenerAdapter}

/**
 * @author Pavel Fatin
 */
// TODO Rely on the immediate UI interaction API when IDEA-123007 will be implemented
class SbtNotificationListener extends ExternalSystemTaskNotificationListenerAdapter {
  override def onTaskOutput(id: ExternalSystemTaskId, text: String, stdOut: Boolean) {
    // TODO this check must be performed in the External System itself (see SCL-7405)
    if (id.getProjectSystemId == SbtProjectSystem.Id) {
      processOutput(text)
    }
  }

  private def processOutput(text: String) {
    text match {
      case WarningMessage(message) =>
        val title = "sbt project import"
        Notifications.Bus.notify(new Notification(title, title, message, NotificationType.WARNING))
      case _ => // do nothing
    }
  }
}

object WarningMessage {
  private val Prefix = "#warning: "

  def apply(text: String): String = Prefix + text

  def unapply(text: String): Option[String] = text.startsWith(Prefix).option(text.substring(Prefix.length))
}
