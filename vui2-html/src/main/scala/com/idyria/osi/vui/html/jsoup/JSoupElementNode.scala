package com.idyria.osi.vui.html.jsoup

import scala.xml.Elem
import com.idyria.osi.vui.html.HTMLNode
import org.jsoup.nodes.Element

class JSoupElementNode[HT <: org.w3c.dom.html.HTMLElement, +Self](val elt: Element,val topwrapper : Option[JSoupElementNode[HT,JSoupElementNode[HT,_]]] = None) extends HTMLNode[HT, Self](nodeName = "wrapper") {
  this: Self =>

  var actualElement = elt
  
  override def toString: String = {

    // transform
    elt.toString()
    
   // return this.xml.toString()

  }
  
}