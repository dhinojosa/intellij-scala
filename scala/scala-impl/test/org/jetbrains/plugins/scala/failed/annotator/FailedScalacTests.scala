package org.jetbrains.plugins.scala.failed.annotator

import java.io.File

import com.intellij.openapi.util.TextRange
import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.projectHighlighting.ScalacTestdataHighlightingTestBase
import org.jetbrains.plugins.scala.util.TestUtils
import org.jetbrains.plugins.scala.util.reporter.ConsoleReporter
import org.junit.experimental.categories.Category

import scala.reflect.NameTransformer


/**
  * Nikolay.Tropin
  * 14-Aug-17
  */
abstract class FailedScalacTestsBase extends ScalacTestdataHighlightingTestBase {

  override lazy val reporter = new ConsoleReporter(filesWithProblems)

  def testDataDir: String = s"${TestUtils.getTestDataPath}/scalacTests/$testDirName/"

  def testDirName: String

  def fileName = getTestName(/*lowercaseFirstLetter*/ false).stripPrefix("_")

  def filesWithProblems: Map[String, Set[TextRange]] = {
    import org.jetbrains.plugins.scala.projectHighlighting._
    getTestName(true) match {
      case "_t7232c" => Map("Test.scala" -> Set())
      case "_t7364b" => Map("UseIt_2.scala" -> Set((68, 79), (56, 64)))
      case "_t4365" => Map("a_1.scala" -> Set((535, 557)))
      case "_t5545" => Map("S_2.scala" -> Set((64, 66)), "S_1.scala" -> Set((64, 66)))
      case "_t6169" => Map("skinnable.scala" -> Set(), "t6169.scala" -> Set())
      case "_t8497" => Map("A_1.scala" -> Set())
      case "_t8934a" => Map("Test_2.scala" -> Set((36, 49)))
      case "_t8781" => Map("Test_2.scala" -> Set((82, 91)))
      case _ => Map((NameTransformer.decode(fileName) + ".scala", Set.empty))
    }
  }

  override def filesToHighlight: Array[File] = {

    val decoded = NameTransformer.decode(fileName)
    val dirPath = testDataDir + decoded
    val dir = new File(dirPath)
    val file = new File(dirPath + ".scala")

    if (dir.exists())
      Array(dir)
    else if (file.exists())
      Array(file)
    else throw new RuntimeException("No file exists")
  }
}

@Category(Array(classOf[PerfCycleTests]))
class FailedScalacTests extends FailedScalacTestsBase {

  def testDirName = "failed"

  //Delete test method and move corresponding .scala file or directory to testdata/scalacTests/pos/ after test passes

  def test_t4365(): Unit = doTest()
  def test_t5545(): Unit = doTest()
  def test_t6169(): Unit = doTest()
  def test_t7232c(): Unit = doTest()
  def test_t7364b(): Unit = doTest()
  def test_t7688(): Unit = doTest()
  def test_t8497(): Unit = doTest()
  def test_arrays3(): Unit = doTest()
  def test_channels(): Unit = doTest()
  def test_compound(): Unit = doTest()
  def `test_cycle-jsoup`(): Unit = doTest()
  def test_depmet_implicit_oopsla_session(): Unit = doTest()
  def test_depmet_implicit_oopsla_session_2(): Unit = doTest()
  def test_depmet_implicit_oopsla_session_simpler(): Unit = doTest()
  def test_fun_undo_eta(): Unit = doTest()
  def `test_gadt-gilles`(): Unit = doTest()
  def test_gadts2(): Unit = doTest()
  def test_hkgadt(): Unit = doTest()
  def `test_implicits-new`(): Unit = doTest()
  def `test_implicits-old`(): Unit = doTest()
  def test_infer_override_def_args(): Unit = doTest()
  def test_infersingle(): Unit = doTest()
  def `test_native-warning`(): Unit = doTest()
  def `test_overloaded-unapply`(): Unit = doTest()
  def test_overloaded_ho_fun(): Unit = doTest()
  def test_presuperContext(): Unit = doTest()
  def `test_reflection-compat-macro-universe`(): Unit = doTest()
  def test_sammy_infer_argtype_subtypes(): Unit = doTest()
  def `test_scala-singleton`(): Unit = doTest()
  def test_t267(): Unit = doTest()
  def test_t389(): Unit = doTest()
  def test_t482(): Unit = doTest()
  def test_t694(): Unit = doTest()
  def test_t697(): Unit = doTest()
  def test_t762(): Unit = doTest()
  def test_t802(): Unit = doTest()
  def test_t1085(): Unit = doTest()
  def test_t1279a(): Unit = doTest()
  def test_t1803(): Unit = doTest()
  def test_t1957(): Unit = doTest()
  def `test_t2712-5`(): Unit = doTest()
  def test_t3177(): Unit = doTest()
  def test_t3866(): Unit = doTest()
  def test_t3880(): Unit = doTest()
  def test_t3999b(): Unit = doTest()
  def test_t5313(): Unit = doTest()
  def test_t5317(): Unit = doTest()
  def test_t5626(): Unit = doTest()
  def test_t5683(): Unit = doTest()
  def test_t5729(): Unit = doTest()
  def test_t5953(): Unit = doTest()
  def test_t5958(): Unit = doTest()
  def test_t6084(): Unit = doTest()
  def test_t6205(): Unit = doTest()
  def test_t6221(): Unit = doTest()
  def test_t6675(): Unit = doTest()
  def test_t6846(): Unit = doTest()
  def test_t7228(): Unit = doTest()
  def test_t7517(): Unit = doTest()
  def test_t7520(): Unit = doTest()
  def test_t7668(): Unit = doTest()
  def test_t7704(): Unit = doTest()
  def test_t7944(): Unit = doTest()
  def `test_t8002-nested-scope`(): Unit = doTest()
  def test_t8079b(): Unit = doTest()
  def test_t8177h(): Unit = doTest()
  def test_t8237(): Unit = doTest()
  def test_t9008(): Unit = doTest()
  def test_t9498(): Unit = doTest()
  def test_t9658(): Unit = doTest()
  def test_ticket2251(): Unit = doTest()
  def `test_typerep-stephane`(): Unit = doTest()
  def test_virtpatmat_gadt_array(): Unit = doTest()
  def test_z1720(): Unit = doTest()

  def test_t7190(): Unit = doTest()
  def `test_macro-bundle-disambiguate-bundle`(): Unit = doTest()
  def `test_macro-bundle-disambiguate-nonbundle`(): Unit = doTest()
}

@Category(Array(classOf[PerfCycleTests]))
class MacrosFailedScalacTests extends FailedScalacTestsBase {
  override def testDirName = "macros"

  def test_t8781(): Unit = doTest()
  def test_t8934a(): Unit = doTest()
  def test_t8523(): Unit = doTest()
}

//these tests pass locally but sometimes fail on teamcity
@Category(Array(classOf[PerfCycleTests]))
class FlakyScalacTests extends FailedScalacTestsBase {
  override def testDirName = "flaky"

  def test_t7516(): Unit = doTest()
  def `test_annotated-treecopy`(): Unit = doTest()
}
