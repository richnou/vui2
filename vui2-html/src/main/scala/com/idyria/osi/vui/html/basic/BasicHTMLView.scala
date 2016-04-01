package com.idyria.osi.vui.html.basic


import java.io.File
import com.idyria.osi.vui.core.view.AViewCompiler
import com.idyria.osi.vui.core.view.AView
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode

 // with StandaloneHTMLUIBuilder
class BasicHTMLView extends AView[HTMLElement,HTMLNode[HTMLElement,HTMLNode[HTMLElement,_]]]  {

}

object BasicHTMLViewCompiler extends AViewCompiler[HTMLElement,BasicHTMLView] {

  /*var eout = new File("target/classes")
  eout.mkdirs()*/
  //compiler.settings2.outputDirs.setSingleOutput(eout.getAbsolutePath)

  this.tempSourceFolder = new File("target/basic-sources")
  this.outputClassesFolder = new File("target/basic-classes")

  this.initCompiler
  
}