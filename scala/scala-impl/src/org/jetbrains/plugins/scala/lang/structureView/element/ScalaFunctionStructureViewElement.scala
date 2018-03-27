package org.jetbrains.plugins.scala.lang.structureView.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScBlockExpr
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScFunction, ScFunctionDefinition}
import org.jetbrains.plugins.scala.lang.psi.types.api.ScTypePresentation
import org.jetbrains.plugins.scala.lang.structureView.ScalaElementPresentation

/**
* @author Alexander Podkhalyuzin
* Date: 04.05.2008
*/

private class ScalaFunctionStructureViewElement(function: ScFunction, inherited: Boolean, val showType: Boolean)
  extends ScalaStructureViewElement(function, inherited) with TypedViewElement {

  override def location: Option[String] = Option(function.containingClass).map(_.name)

  override def getPresentableText: String = {
    val presentation = ScalaElementPresentation.getMethodPresentableText(function)

    val inferredType =
      if (function.isConstructor) None
      else function.returnTypeElement match {
        case Some(_) => None
        case None => if (showType) function.returnType.toOption.map(ScTypePresentation.withoutAliases) else None
      }

    ScalaItemPresentation.withSimpleNames(presentation + inferredType.map(": " + _).mkString)
  }

  override def getChildren: Array[TreeElement] = function match {
    case definition: ScFunctionDefinition => definition.body match {
      case Some(block: ScBlockExpr) => ScalaStructureViewElement(block, inherited).flatMap(_.getChildren).toArray
      case _ => TreeElement.EMPTY_ARRAY
    }
    case _ => TreeElement.EMPTY_ARRAY
  }
}