package com.idyria.osi.vui.html.basic

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode
import scala.xml.Elem

trait DefaultBasicHTMLBuilder extends BasicHTMLBuilderTrait[HTMLElement] {
  
  def content(cl: => Any) = {
    
    //--
    cl
    
  }
  
  def importHTML(xml:Elem) = {
    
    wrapper(xml.toString()){
      
    }
  }
  
  def +(xml:Elem) = {
    
    wrapper(xml.toString()){
      
    }
  }
  
}