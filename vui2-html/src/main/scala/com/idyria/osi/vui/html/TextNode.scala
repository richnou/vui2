package com.idyria.osi.vui.html

class TextNode[HT <: org.w3c.dom.html.HTMLElement, +Self](t:String)  extends HTMLNode[HT, Self](nodeName = "") {
  this:Self => 
    this.textContent = t
    
    override def toString = {
      this.textContent
    }
    

}