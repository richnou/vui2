package com.idyria.osi.vui.html.basic

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode
import scala.xml.Elem
import java.net.URL
import com.idyria.osi.vui.html.Wrapper
import java.net.URI
import com.idyria.osi.vui.html.Script
import com.idyria.osi.vui.html.Div
import com.idyria.osi.vui.html.P
import com.idyria.osi.vui.html.Thead

trait DefaultBasicHTMLBuilder extends BasicHTMLBuilderTrait[HTMLElement] {

  // Content API
  //----------------
  def content(cl: => Any) = {

    //--
    cl

  }

  // Import XML Parsed Stuff
  //----------
  def importHTML(xml: Elem) = {

    wrapper(xml.toString()) {

    }
  }

  def $(xml: Elem): Wrapper[HTMLElement, Wrapper[HTMLElement, _]] = importHTML(xml)
  def $(str: String): P[HTMLElement, _] = {
    p {
      textContent(str)
    }
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

}

object DefaultBasicHTMLBuilder {
  implicit class NoDoubleSlash(val underlying: String) extends AnyVal {
    def noDoubleSlash: String = underlying.replaceAll("//+", "/")
  }

  //implicit def c(u:String) = new NoDoubleSlash(u)

}