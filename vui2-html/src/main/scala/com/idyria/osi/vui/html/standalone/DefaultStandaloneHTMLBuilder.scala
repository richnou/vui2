package com.idyria.osi.vui.html.standalone

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.Head
import com.idyria.osi.vui.html.basic.DefaultBasicHTMLBuilder

import com.idyria.osi.vui.html.HTMLNode
import netscape.javascript.JSObject
import com.idyria.osi.vui.html.Script
import com.idyria.osi.tea.thread.ThreadLanguage
import org.apache.commons.lang3.RandomUtils
import scala.util.Random

trait DefaultStandaloneHTMLBuilder extends StandaloneBasicHTMLBuilderTrait[HTMLElement] with DefaultBasicHTMLBuilder with ThreadLanguage {

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
      case e: Script[_, _] if (e.src.toString.contains("jquery")) =>
        node.children.indexOf(e)
    } match {
      case Some(jqueryNodeIndex) =>
        switchToNode(node, {
          println(s"FOUND JQUERY")
          var resScript = script(getClass.getClassLoader.getResource("standalone/vui-standalone.js").toURI()) {

          }

          //-- Move
          //node.sgChildren
          // node.sgChildren = node.children.take(jqueryNodeIndex) ::: List(resScript) ::: node.children.takeRight(node.children.size-jqueryNodeIndex).dropRight(1)
        })
      case None =>
        switchToNode(node, {
          script(getClass.getClassLoader.getResource("standalone/vui-standalone.js").toURI()) {

          }
        })
    }

    //-- Return
    node

  }

  override def content(cl: => Any) = {

    //-- Current Node is going to be standalone
    val currentStandaloneNode = currentNode.asInstanceOf[StandaloneHTMLNode[HTMLElement, StandaloneHTMLNode[HTMLElement, _]]]

    //-- Register Closure
    var clid: String = currentStandaloneNode.getRootParent.asInstanceOf[StandaloneHtml[HTMLElement, _]].registerClosure {
      _ =>

        //-- Make sure not is bound and content as well
        /* bind
        
        //-- Set Content to Element
        currentStandaloneNode.+@("vui-content" -> clid)*/

        //-- Switch to node and run closure
        //-- Run content again as well to make sure we can reupdate a second time
        switchToNode(currentStandaloneNode, {
          content(cl)
          //cl
        })

      //
    }

    //-- Set to Element
    currentStandaloneNode.+@("vui-content" -> clid)

    //-- Force bind
    bind

    //-- Run Closure
    //println(s"Run");
    super.content(cl)
    //cl

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
        // println(s"Scala Binding ${currentStandaloneNode.nodeName} to $obj -> ${obj.getClass().getCanonicalName}")
        currentStandaloneNode.delegate = obj.asInstanceOf[HTMLElement]
      //this.delegate = obj.asInstanceOf[JSObject]
      //this.@->("bound")

      //
    }
    currentStandaloneNode.+@("vui-bind" -> clid)

    // Set Id if necessary
    currentStandaloneNode.attributes.contains("id") match {
      case true =>

      case false =>
        currentStandaloneNode("id" -> (Random.nextGaussian() * 10000).toInt)
    }
  }

  // Events
  //-----------
  def onChange(cl: String => Any) = {

    //-- Force binding
    bind

    //-- Current Node is going to be standalone
    var currentStandaloneNode = currentNode.asInstanceOf[StandaloneHTMLNode[HTMLElement, StandaloneHTMLNode[HTMLElement, _]]]

    //-- Get Base
    var base = currentStandaloneNode.getRootParent.asInstanceOf[StandaloneHtml[HTMLElement, _]]

    var clid = base.registerClosure {
      obj =>
        obj match {
          case input: com.sun.webkit.dom.HTMLInputElementImpl => cl(input.getValue)
          case select: com.sun.webkit.dom.HTMLSelectElementImpl => cl(select.getValue)
        }
      //obj.asInstanceOf[com.sun.webkit.dom.HTMLInputElementImpl].getValue
      //println(s"Changed: " + obj.asInstanceOf[HTMLElement].getAttribute("value"))
    }
    currentStandaloneNode("onchange" -> s"base.call('$clid',this)")
  }

  override def onClick(cl: => Unit) = {

    bind

    //-- Current Node is going to be standalone
    val currentStandaloneNode = currentNode.asInstanceOf[StandaloneHTMLNode[HTMLElement, StandaloneHTMLNode[HTMLElement, _]]]

    /*var newCl = {
      
      
    }*/
    super.onClick {
      var th = createThread {
        try {
          currentStandaloneNode.base.setAttribute("disabled", "true")
          cl
        } finally {
         // currentStandaloneNode.base.setAttribute("disabled", "false")
        }
      }
      th.setDaemon(true)
      th.start
    }

  }

}