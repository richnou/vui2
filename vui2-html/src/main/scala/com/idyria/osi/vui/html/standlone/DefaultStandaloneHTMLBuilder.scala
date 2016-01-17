package com.idyria.osi.vui.html.standlone

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.standalone.StandaloneBasicHTMLBuilderTrait
import com.idyria.osi.vui.html.Head
import com.idyria.osi.vui.html.basic.DefaultBasicHTMLBuilder
import com.idyria.osi.vui.html.standalone.StandaloneHTMLNode
import com.idyria.osi.vui.html.standalone.StandaloneHtml
import com.idyria.osi.vui.html.HTMLNode
import netscape.javascript.JSObject
import com.idyria.osi.vui.html.Script

trait DefaultStandaloneHTMLBuilder extends StandaloneBasicHTMLBuilderTrait[HTMLElement] with DefaultBasicHTMLBuilder {

  override def head(cl: => Any) = {

    // Create if necessary 
    var node = currentNode.children.collectFirst {
      case e: Head[HTMLElement, Head[HTMLElement, _]] => e
    } match {
      case Some(header) => header
      case None => createHead
    }

    // Run Closure 
    switchToNode(node, cl)

    //-- Add Standalone Script
    //-- 1. Add First, or After JQuery
    node.children.collectFirst {
      case e : Script[_,_] if(e.src.toExternalForm().contains("jquery")) => 
        node.children.indexOf(e)
    } match {
      case Some(jqueryNodeIndex) => 
        switchToNode(node, {
          println(s"FOUND JQUERY")
          var resScript = script(getClass.getClassLoader.getResource("standalone/vui-standalone.js")) {
            
          }
          
          //-- Move
          //node.sgChildren
         // node.sgChildren = node.children.take(jqueryNodeIndex) ::: List(resScript) ::: node.children.takeRight(node.children.size-jqueryNodeIndex).dropRight(1)
        })
      case None => 
        switchToNode(node, {
          script(getClass.getClassLoader.getResource("standalone/vui-standalone.js")) {
            
          }
        })
    }
    
    //-- Return
    node

  }

  override def content(cl: => Any) = {

    //-- Current Node is going to be standalone
    var currentStandaloneNode = currentNode.asInstanceOf[StandaloneHTMLNode[HTMLElement, StandaloneHTMLNode[HTMLElement, _]]]

    //-- Register Closure
    var clid = currentStandaloneNode.getRootParent.asInstanceOf[StandaloneHtml[HTMLElement, _]].registerClosure {
      _ =>

        //-- Switch to node and run closure
        switchToNode(currentStandaloneNode, cl)

      //
    }

    //-- Set to Element
    currentStandaloneNode.+@("vui-content" -> clid)
    
    //-- Force bind
    bind
    //-- Run Closure
    cl

  }

  def bind = {

    //-- Current Node is going to be standalone
    var currentStandaloneNode = currentNode.asInstanceOf[StandaloneHTMLNode[HTMLElement, StandaloneHTMLNode[HTMLElement, _]]]

    //-- Get Base
    var base = currentStandaloneNode.getRootParent.asInstanceOf[StandaloneHtml[HTMLElement, _]]
    
    //-- Register
     var clid = base.registerClosure {
      obj =>

        //-- Switch to node and run closure
        //switchToNode(currentStandaloneNode, cl)
        println(s"Scala Binding ${currentStandaloneNode.nodeName} to $obj -> ${obj.getClass().getCanonicalName}")
        currentStandaloneNode.delegate = obj.asInstanceOf[HTMLElement]
        //this.delegate = obj.asInstanceOf[JSObject]
        //this.@->("bound")

      //
    }
    currentStandaloneNode.+@("vui-bind" -> clid)
  }

}