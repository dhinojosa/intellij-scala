package org.jetbrains.plugins.scala.lang.structureView.elements.impl

import javax.swing._

import com.intellij.navigation.ColoredItemPresentation
import com.intellij.openapi.editor.colors.{CodeInsightColors, TextAttributesKey}
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.util.ui.UIUtil

/**
* @author Alexander Podkhalyuzin
* Date: 04.05.2008
*/
trait ScalaItemPresentation extends ColoredItemPresentation {
  def element: PsiElement

  def inherited: Boolean

  override final def getLocationString: String =
    if (inherited) location.map(UIUtil.rightArrow + _).orNull else null

  protected def location: Option[String] = None

  override def getIcon(open: Boolean): Icon =
    element.getIcon(Iconable.ICON_FLAG_VISIBILITY)

  override def getTextAttributesKey: TextAttributesKey =
    if (inherited) CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES else null
}

private[elements] object ScalaItemPresentation {
  private val FullyQualifiedName = "(?:\\w+\\.)+(\\w+)".r

  private[elements] def withSimpleNames(presentation: String): String =
    FullyQualifiedName.replaceAllIn(presentation, "$1")
}