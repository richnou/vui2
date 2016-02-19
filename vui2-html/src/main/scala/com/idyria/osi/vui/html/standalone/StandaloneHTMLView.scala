package com.idyria.osi.vui.html.standalone


import java.io.File
import com.idyria.osi.vui.core.view.AViewCompiler
import com.idyria.osi.vui.core.view.AView
import com.idyria.osi.vui.html.standalone.StandaloneHTMLNode
import org.w3c.dom.html.HTMLElement

 // with StandaloneHTMLUIBuilder
class StandaloneHTMLView extends AView[HTMLElement,StandaloneHTMLNode[HTMLElement,StandaloneHTMLNode[HTMLElement,_]]]  {

}

object StandaloneHTMLViewCompiler extends AViewCompiler[HTMLElement,StandaloneHTMLView] {

  var eout = new File("target/classes")
  eout.mkdirs()
  compiler.settings2.outputDirs.setSingleOutput(eout.getAbsolutePath)

}