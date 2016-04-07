package com.idyria.osi.vui.core.view

import java.io.File
import java.net.URL

import org.scalatest.FunSuite

import com.idyria.osi.tea.compile.ClassDomain
import com.idyria.osi.vui.core.definitions.VUISGNode




class AViewTest extends FunSuite {
  
  
  
  test("View compile without ref Object fails") {
    
    // Prepar Compiler and Class Loader
    var c = new AViewCompiler[DummyBack,ViewClass]()
    c.standardSearchPath = new File("src/test/scala")
    c.initCompiler
    Thread.currentThread().setContextClassLoader(new ClassDomain(Array(c.outputClassesFolder.toURI().toURL()),Thread.currentThread().getContextClassLoader))
    
    //c.createView(classOf[TestViewBaseClass], false)
    
    intercept[RuntimeException] {
       c.newInstance(None, classOf[TestViewBaseClass])
    }
    
    
  }
  
  test("View Compile view ref object succeeds") {
    
    var c = new AViewCompiler[DummyBack,ViewClass]()
    c.standardSearchPath = new File("src/test/scala")
    c.initCompiler
    Thread.currentThread().setContextClassLoader(new ClassDomain(Array(c.outputClassesFolder.toURI().toURL()),Thread.currentThread().getContextClassLoader))
    
    var ro = new TestViewBaseClass(new SpecialTestObj)
    var newinst = c.newInstance(Some(ro),classOf[TestViewBaseClass])
    assertResult(ro.title)(newinst.title)
    
  }
  import scala.reflect.runtime.universe._
  test("View Compile view ref object succeeds over compiler") {
    
    
    var c = new AViewCompiler[DummyBack,ViewClass]()
    c.standardSearchPath = new File("src/test/")
    c.initCompiler
    Thread.currentThread().setContextClassLoader(new ClassDomain(Array(c.outputClassesFolder.toURI().toURL()),Thread.currentThread().getContextClassLoader))
    
    var ro = new TestViewBaseClass(new SpecialTestObj)
    
    println(s"Type: "+typeTag[String].mirror.classSymbol(ro.getClass).asType.name)
    
    var newinst = c.createView(Some(ro),classOf[TestViewBaseClass],false)
    assertResult(ro.title)(newinst.title)
    
  }
  
}