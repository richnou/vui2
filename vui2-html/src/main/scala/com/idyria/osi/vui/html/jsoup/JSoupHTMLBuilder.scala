package com.idyria.osi.vui.html.jsoup

import com.idyria.osi.vui.html.basic.DefaultBasicHTMLBuilder
import org.jsoup.Jsoup
import org.w3c.dom.html.HTMLElement

trait JSoupHTMLBuilder extends DefaultBasicHTMLBuilder {
  
  def jsoupHTML(str:String)(cl: => Any) = {
    
    var jsNode = new JSoupElementNode[HTMLElement,JSoupElementNode[_,_]](Jsoup.parse(str))
    switchToNode(jsNode, cl)
    
  }
  
}