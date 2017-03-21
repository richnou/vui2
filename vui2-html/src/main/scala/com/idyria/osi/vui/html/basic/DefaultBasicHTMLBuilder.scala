package com.idyria.osi.vui.html.basic

import java.net.URI

import scala.xml.Elem

import org.w3c.dom.html.HTMLElement

import com.idyria.osi.vui.html.Div
import com.idyria.osi.vui.html.HTMLNode
import com.idyria.osi.vui.html.Input
import com.idyria.osi.vui.html.Label
import com.idyria.osi.vui.html.P
import com.idyria.osi.vui.html.Script
import com.idyria.osi.vui.html.Span
import com.idyria.osi.vui.html.Td
import com.idyria.osi.vui.html.Thead
import com.idyria.osi.vui.html.Tr
import com.idyria.osi.vui.html.Wrapper
import com.idyria.osi.vui.html.xml.XMLHTMLNode
import com.idyria.osi.vui.html.TextNode

trait DefaultBasicHTMLBuilder extends BasicHTMLBuilderTrait[HTMLElement] {

  // Parts
  //---------------
  // Placeholder
  //--------------
  var placesMap = Map[String,() => HTMLNode[HTMLElement,_]]()
  
  def placePart(id:String)  = {
    placesMap.get(id) match {
      case Some(cl) => 
       switchToNode(cl(),{})
        
      case None => 
        
        throw new RuntimeException("Cannot place part: "+id+" because it hasn't been defined")
    }
  }
  
  def definePart(id:String)(cl: =>HTMLNode[HTMLElement,_]) {
    placesMap = placesMap + (id ->{ () =>  cl })
  }
  
  
  
  
  // Content API
  //----------------

  def content(cl: => Any) = {

    //--
    cl

  }

  /**
   * Adds an attribute only if #test is true
   * Useful for attributes like "selected" or "checked" which are active on presence not value
   */
  def attributeIf(test: Boolean)(attributeName: String,attributeValue:String = "") = {
    if (test) {
      +@(attributeName -> attributeValue)
    }

  }
  
  /**
   * Adde data atribute
   */
 def data(nameValue:(String , Any)) = {
   +@("data-"+nameValue._1 -> nameValue._2.toString())
 }
  
  // Text
  //--------------
  def span(str:String) :  Span[HTMLElement, Span[HTMLElement, _]] = {
      span {
        textContent(str)
      }
  }
  def p(str:String) :  P[HTMLElement, P[HTMLElement, _]] = {
      p {
        textContent(str)
      }
  }
  

  // Forms
  //--------------------

  override def label(str: String)(cl: => Any): Label[HTMLElement, Label[HTMLElement, _]] = {

    // Create
    //--------------------
    val labelElement = this.createLabel(str)

    // Run closure
    switchToNode(labelElement, cl)

    // Special Features:
    //-----------

    //-- If an input element is present as child, remove it and add to current node
    labelElement.children.collect { case node if (node.isInstanceOf[Input[HTMLElement, _]]) => node.asInstanceOf[Input[HTMLElement, _]] }.foreach {
      inputElement =>

        // Detach and readd
        inputElement.detach
        switchToNode(inputElement, {})

        // If input Element has an ID, and for none, use it on for
        (inputElement.attributeOption("id"),labelElement.attributeOption("id")) match {
          case (Some(id),None) =>
            +@("for" -> id.toString())
          case _ =>
        }
    }

    labelElement
  }

  /**
   * Improve input element to allow non valid HTML constructs
   */
  override def input(cl: => Any): Input[HTMLElement, Input[HTMLElement, _]] = {

    // Create
    //--------------------
    val inputElement = this.createInput

    // Run closure
    switchToNode(inputElement, cl)

    // Add special Features like Label adjustment
    //-------------------------

    //---- Labels
    inputElement.children.find(node => node.isInstanceOf[Label[HTMLElement, _]]) match {
      case Some(label) =>

        // Move to label input container
        // Move Label before input element by removing input, then readd it after label
        // Use input ID for "for" attribute
        inputElement.detach
        move(label.asInstanceOf[HTMLNode[HTMLElement, _]]) {

          inputElement.attributeOption("id") match {
            case Some(id) =>
              +@("for" -> id.toString())
            case None =>
          }
        }
        switchToNode(inputElement, {})

      case None =>
    }

    inputElement

  }

  // Import XML Parsed Stuff
  //----------
  def importHTML(xml: Elem) = {

    switchToNode(new XMLHTMLNode[HTMLElement, XMLHTMLNode[HTMLElement, _]](xml),{})
  }

  def $(xml: Elem)(cl: => Any): XMLHTMLNode[HTMLElement, XMLHTMLNode[HTMLElement, _]] = {
    switchToNode(new XMLHTMLNode[HTMLElement, XMLHTMLNode[HTMLElement, _]](xml),cl)
  }
  
  
  /**
   * Selector
   */
  def $(selector:String): Option[HTMLNode[HTMLElement,HTMLNode[_,_]]] = {
    currentNode.select(selector)
  }
  /*def +(xml:Elem) = {
    
    wrapper(xml.toString()){
      
    }
  }*/

  // Manipulate Tree
  //-----------
  def move(n: HTMLNode[HTMLElement, _])(cl: => Any) = {
    n.detach
    this.switchToNode(n, {
      cl
    })
  }

  def moveWithContent(n: HTMLNode[HTMLElement, _])(cl: => Any) = {
    n.detach
    this.switchToNode(n, {
      content(cl)
    })
  }

  // Script
  //---------------

  def script(s: String): Script[HTMLElement, Script[HTMLElement, _]] = {
    script(new URI("")) {
      currentNode.attributes = currentNode.attributes.empty
      textContent(s)
    }
    /* var resScript = this.createScript(new URI(""))
    resScript.textContent = s*/
  }

  // Text
  //--------------
  def text(str:String) = {
    var tn = new TextNode[HTMLElement,TextNode[HTMLElement,_]](str)
    switchToNode(tn, {})
    tn
  }
  
  // Auto Converts
  //-------------------
  implicit def strToDiv(str: String): Div[HTMLElement, Div[HTMLElement, _]] = {

    var d = div {
      textContent(str)
    }

    d
  }

  // Table
  //-------------------
  
  def rtd(cl : => Unit) : Td[HTMLElement, Td[HTMLElement, _]]= {
    td("") {
      cl
    }
  }
  
  def thead(headers: String*): Thead[HTMLElement, Thead[HTMLElement, _]] = {
    thead {
      tr {
        headers.foreach {
          hn =>
            th(hn) {

            }
        }
      }

    }
  }
  
  def trvalues(values:Any*) : Tr[HTMLElement, Tr[HTMLElement, _]] = {
    
    tr {
      values.foreach {
        case v : HTMLNode[_,_] => 
          v.detach
          switchToNode(v.asInstanceOf[HTMLNode[HTMLElement,_]],{})
          
        case null => 
        case v => td(v.toString()) {
          
        }

      }
    }
    
  }
  
  /**
   * Create a tr with one td and cl int td
   */
  def trtd(text:String)(cl: => Any) = {
    tr {
      td(text) {
        cl
      }
    }
  }
  
  def colspan(v:Int) = {
    +@("colspan" -> v)
  }

}

object DefaultBasicHTMLBuilder {
  implicit class NoDoubleSlash(val underlying: String) extends AnyVal {
    def noDoubleSlash: String = underlying.replaceAll("//+", "/")
  }

  //implicit def c(u:String) = new NoDoubleSlash(u)

}