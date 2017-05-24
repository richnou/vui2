package com.idyria.osi.vui.core.view

import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import java.io.File
import java.nio.file.Files
import java.nio.file.CopyOption
import java.nio.file.StandardCopyOption
import com.idyria.osi.tea.logging.TLog
import org.scalatest.BeforeAndAfter
import org.scalatest.Ignore

@Ignore
class AViewAutoReloadTest extends FunSuite with GivenWhenThen with BeforeAndAfter {

  var compiler: AViewCompiler[DummyBack, ViewClass] = null
  var targetFile: File = null;

  before {
    compiler = new AViewCompiler[DummyBack, ViewClass]()
    compiler.initCompiler
    compiler.standardSearchPath = new File("target/test-generated-resources/scala")
    compiler.standardSearchPath.mkdirs

    // Copy base class to generated to avoid conflicts
    targetFile = new File(compiler.standardSearchPath, "/com/idyria/osi/vui/core/view/TestViewBaseClass.scala")
    targetFile.getParentFile.mkdirs()
    Files.copy(new File("src/test/scala/com/idyria/osi/vui/core/view/TestViewBaseClass.scala").toPath,
      targetFile.toPath,
      StandardCopyOption.REPLACE_EXISTING)
  }

  test("AutoReload Registers only once") {

    var reloadCount = 0

    TLog.setLevel(classOf[AViewCompiler[_, _]], TLog.Level.FULL)

    //-- Update Reload counter on autoreload
    compiler.on("reload") {
      reloadCount += 1
    }

    Given("A Base Class")
    When("Registered for Autoreload")
    compiler.autoReloadFor(classOf[TestViewBaseClass])

    Then("Triggering Autoreload triggers one autoreload")
    Thread.sleep(100)
    targetFile.setLastModified(System.currentTimeMillis())
    Thread.sleep(1000)
    assertResult(1)(reloadCount)

    And("When Registering a second time")
    compiler.autoReloadFor(classOf[TestViewBaseClass])
    Then("Triggering Autoreload triggers one more autoreload")
    Thread.sleep(100)
    targetFile.setLastModified(System.currentTimeMillis())
    Thread.sleep(1000)
    assertResult(2)(reloadCount)

  }

  test("AutoReload Signals to object, then no more when object is removed") {
    
    TLog.setLevel(classOf[AViewCompiler[_, _]], TLog.Level.FULL)
    TLog.setLevel(classOf[AView[_, _]], TLog.Level.FULL)
    
    var reloadCount = 0

    var view = new TestViewBaseClass(new SpecialTestObj)
    view.onWith("view.replace") {
      v:AView[_, _] =>
        reloadCount += 1
    }

    Given("A Base Object")
    When("Registered for Autoreload")
    compiler.registerView(classOf[TestViewBaseClass],view)
    Then("Compiler registration list is not empty")
    assertResult(view)(compiler.autoReloadRegistration.get(view.getClass).get.head.get)

    And("File is modified")
    Thread.sleep(100)
    targetFile.setLastModified(System.currentTimeMillis())
    Thread.sleep(2000)
    Then("view.replace is called on view")
    assertResult(1)(reloadCount)

    When("A second view is added and file modified, one more reload should be seen because first view has been replace")
    var view2 = new TestViewBaseClass(new SpecialTestObj)
    view2.onWith("view.replace") {
      v:AView[_, _] =>
        reloadCount += 1
    }
    compiler.registerView(classOf[TestViewBaseClass],view2)
    Thread.sleep(100)
    targetFile.setLastModified(System.currentTimeMillis())
    Thread.sleep(500)
    assertResult(2)(reloadCount)

    //-- Clean one 
    view = null
    System.gc
    System.gc
    When("One View is removed, and file is modifier, one more reload should be seen")
    Thread.sleep(100)
    targetFile.setLastModified(System.currentTimeMillis())
    Thread.sleep(500)
    assertResult(2)(reloadCount)

    //-- Clean everything
    /*view2 = null
    System.gc
    System.gc*/
    view2.@->("clean")
    When("One View is removed, and file is modifier, one more reload should be seen")
    Thread.sleep(100)
    targetFile.setLastModified(System.currentTimeMillis())
    Thread.sleep(5100)
    assertResult(2)(reloadCount)
  }

}