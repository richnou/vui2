package com.idyria.osi.vui.html.basic

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode
import scala.xml.Elem
import java.net.URL
import com.idyria.osi.vui.html.Wrapper
import java.net.URI
import com.idyria.osi.vui.html.Script

trait DefaultBasicHTMLBuilder extends BasicHTMLBuilderTrait[HTMLElement] {
  
  // Content API
  //----------------
  def content(cl: => Any) = {
    
    //--
    cl
    
  }
  
  // Import XML Parsed Stuff
  //----------
  def importHTML(xml:Elem) = {
    
    wrapper(xml.toString()){
      
    }
  }
  
  def $(xml:Elem) : Wrapper[HTMLElement, Wrapper[HTMLElement, _]]  = importHTML(xml)
  
  /*def +(xml:Elem) = {
    
    wrapper(xml.toString()){
      
    }
  }*/
  
  // Manipulate Tree
  //-----------
  def move(n:HTMLNode[HTMLElement,_])(cl: => Any) = {
    n.detach
    this.switchToNode(n, {
      cl
    })
  }
  
  def moveWithContent(n:HTMLNode[HTMLElement,_])(cl: => Any) = {
    n.detach
    this.switchToNode(n, {
      content(cl)
    })
  }
  
  // Script
  //---------------
  
  def script(s:String) : Script[HTMLElement, Script[HTMLElement, _]] =  {
    script(new URI("")) {
      currentNode.attributes = currentNode.attributes.empty
      textContent(s)
    }
   /* var resScript = this.createScript(new URI(""))
    resScript.textContent = s*/
  }
  
  
  
}