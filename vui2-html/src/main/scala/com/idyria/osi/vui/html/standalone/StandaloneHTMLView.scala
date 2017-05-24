package com.idyria.osi.vui.html.standalone

import java.io.File

import org.w3c.dom.html.HTMLElement

import com.idyria.osi.vui.core.view.AView
import com.idyria.osi.vui.core.view.AViewCompiler

 // with StandaloneHTMLUIBuilder
class StandaloneHTMLView extends AView[HTMLElement,StandaloneHTMLNode[HTMLElement,StandaloneHTMLNode[HTMLElement,_]]]  {

}

object StandaloneHTMLViewCompiler extends AViewCompiler[HTMLElement,StandaloneHTMLView] {


  this.tempSourceFolder = new File("target/standalone-sources")
  this.outputClassesFolder = new File("target/standalone-classes")

  this.initCompiler
  
}