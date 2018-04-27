package org.jetbrains.bsp

import java.io.File

import com.intellij.compiler.CompilerConfiguration
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.model.{DataNode, Key, ProjectSystemId}
import com.intellij.openapi.externalSystem.service.notification.{ExternalSystemNotificationManager, NotificationCategory, NotificationData, NotificationSource}
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.{LanguageLevelModuleExtensionImpl, OrderRootType}
import com.intellij.openapi.vfs.VfsUtil
import org.jetbrains.plugins.scala.project._
import org.jetbrains.plugins.scala.project.external.{AbstractImporter, SdkReference, SdkUtils}


class ScalaSdkService extends AbstractProjectDataService[ScalaSdkData, Library] {
  def getTargetDataKey: Key[ScalaSdkData] = ScalaSdkData.Key

  import scala.collection.JavaConverters._

  override final def importData(toImport: java.util.Collection[DataNode[ScalaSdkData]],
                                projectData: ProjectData,
                                project: Project,
                                modelsProvider: IdeModifiableModelsProvider): Unit = {

    new ScalaSdkService.Importer(toImport.asScala.toSeq, projectData, project, modelsProvider)
  }

}

object ScalaSdkService {

  private class Importer(dataToImport: Seq[DataNode[ScalaSdkData]],
                         projectData: ProjectData,
                         project: Project,
                         modelsProvider: IdeModifiableModelsProvider)
    extends AbstractImporter[ScalaSdkData](dataToImport, projectData, project, modelsProvider) {

    override def importData(): Unit =
      dataToImport.foreach(doImport)

    private def doImport(dataNode: DataNode[ScalaSdkData]): Unit = {
      for {
        module <- getIdeModuleByNode(dataNode)
        data = dataNode.getData
      } {
        module.configureScalaCompilerSettingsFrom("bsp", data.scalacOptions)
        data.scalaVersion.foreach(
          version => configureScalaSdk(module, data.scalaOrganization, version, data.scalacClasspath))
        configureOrInheritSdk(module, data.jdk)
        configureLanguageLevel(module, data.javacOptions)
        configureJavacOptions(module, data.javacOptions)
      }
    }

    private def configureScalaSdk(module: Module,
                                  compilerOrganization: String,
                                  compilerVersion: Version,
                                  compilerClasspath: Seq[File]): Unit = {

      val platform = compilerOrganization match {
        case "ch.epfl.lamp" => Platform.Dotty
        case _ => Platform.Scala
      }
      val languageLevel = compilerVersion.toLanguageLevel.get
      val scalaLibrary: Library = createScalaLibrary(module, compilerClasspath)

      setScalaSdk(scalaLibrary, platform, languageLevel, compilerClasspath)
    }

    private def createScalaLibrary(module: Module, compilerClasspath: Seq[File]): Library = {
      val rootModel = getModifiableRootModel(module)
      val scalaLib = rootModel.getModuleLibraryTable.createLibrary("scala-library")
      val libraryModel = scalaLib.getModifiableModel
      compilerClasspath
        .filter(_.getName.contains("scala-library"))
        .foreach { file =>
          val vfile = VfsUtil.findFileByIoFile(file, true)
          libraryModel.addRoot(vfile, OrderRootType.CLASSES)
        }
      libraryModel.commit()
      scalaLib
    }

    private def configureOrInheritSdk(module: Module, sdk: Option[SdkReference]): Unit = {
      val model = getModifiableRootModel(module)
      model.inheritSdk()
      sdk.flatMap(SdkUtils.findProjectSdk).foreach(model.setSdk)
    }

    private def configureLanguageLevel(module: Module, javacOptions: Seq[String]): Unit = {
      val model = getModifiableRootModel(module)
      val moduleSdk = Option(model.getSdk)
      val languageLevel = SdkUtils.javaLanguageLevelFrom(javacOptions)
        .orElse(moduleSdk.flatMap(SdkUtils.defaultJavaLanguageLevelIn))
      languageLevel.foreach { level =>
        val extension = model.getModuleExtension(classOf[LanguageLevelModuleExtensionImpl])
        extension.setLanguageLevel(level)
      }
    }

    private def configureJavacOptions(module: Module, javacOptions: Seq[String]): Unit = {
      for {
        targetPos <- Option(javacOptions.indexOf("-target")).filterNot(_ == -1)
        targetValue <- javacOptions.lift(targetPos + 1)
        compilerSettings = CompilerConfiguration.getInstance(module.getProject)
      } {
        executeProjectChangeAction(compilerSettings.setBytecodeTargetLevel(module, targetValue))
      }
    }

    private def showWarning(message: String): Unit = {
      val notification = new NotificationData("bsp Import", message, NotificationCategory.WARNING, NotificationSource.PROJECT_SYNC)
      notification.setBalloonGroup("bsp")
      if (ApplicationManager.getApplication.isUnitTestMode) {
        throw NotificationException(notification, bsp.ProjectSystemId)
      } else {
        ExternalSystemNotificationManager.getInstance(project).showNotification(bsp.ProjectSystemId, notification)
      }
    }

  }

  case class NotificationException(data: NotificationData, id: ProjectSystemId) extends Exception

}