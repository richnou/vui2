package com.idyria.osi.vui.html.test

import com.idyria.osi.vui.html.standalone.StandaloneBasicHTMLBuilderTrait
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.lib.semanticui.SemanticUIBuilder


object VUI2HTMLTry extends App  with StandaloneBasicHTMLBuilderTrait[HTMLElement] with SemanticUIBuilder   {
  
  
 
  println(s"VUI")
  
  var htmlNode = html {
    
   /* head {
      
    }
    var b = body {
      
    }
    
    var b2 = "class" :: b */
    
    /*var t = new Test[_,Test[_,_]]
    var t2 = "class" :: t
    
    var st = new SubTest[org.w3c.dom.html.HTMLElement,SubTest[org.w3c.dom.html.HTMLElement,_]]
    var st2 = "class" :: st*/
  }
  
  println(s"Node: $htmlNode")
}