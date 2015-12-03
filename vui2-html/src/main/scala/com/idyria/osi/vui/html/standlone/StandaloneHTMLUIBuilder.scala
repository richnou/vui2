package com.idyria.osi.vui.html.standlone

import com.idyria.osi.vui.html.A
import com.idyria.osi.vui.core.builders.RebuildableNode
import com.idyria.osi.vui.html.InputText
import java.awt.Button
import com.idyria.osi.vui.html.Html
import com.idyria.osi.vui.html.Head
import com.idyria.osi.vui.html.HTMLNode
import com.idyria.osi.vui.html.Body
import com.idyria.osi.vui.html.Div

/*

trait StandaloneHTMLNode2[BT <: org.w3c.dom.html.HTMLElement,+Self <: StandaloneHTMLNode2[BT,Self]] extends HTMLNode[BT,Self] {

  this : Self => 
    
  
  //this("onload" -> "base.bin)

  // Rebuild
  //---------------------
  override def clear = {
    super.clear
    /*this.getRootParent.asInstanceOf[StandaloneHTML].engine match {
          case None => throw new RuntimeException("Cannot Clear HTML, no engine set on parent HTML")
          case Some(engine) =>
            //engine.
    }*/
  }
  this.on("rebuild") {
    this.base match {
      case null =>
      case dom =>

        // Get string
        var newHTML = this.toString()

       println(s"Parsing: ${newHTML}");
        
        // Parse using JS
        this.getRootParent.asInstanceOf[StandaloneHTML].engine match {
          case None => throw new RuntimeException("Cannot Rebuild HTML, no engine set on parent HTML")
          case Some(engine) =>

            // wrapper.innerHTML="${newHTML.trim.replace("\"","'").replace("\n", "")}"
//            wrapper.innerHTML="<div>\\(ax^2 + bx + c = 0\\)</div>"
            var elt = engine.executeScript(s"""
var wrapper= document.createElement('div');
wrapper.innerHTML="${newHTML.trim.replace("\"","'").replace("\n", "")}"
wrapper.firstChild
""")
            println(s"Parsed: $elt")

            var newDOM = dom.getOwnerDocument.importNode(elt.asInstanceOf[org.w3c.dom.Node], true).asInstanceOf[DT]
            var parent = dom.getParentNode
            parent.replaceChild(newDOM,dom)
            
            this.delegate = newDOM
            
            /*onUIThread {
              println(s"Redoing init")
              engine.executeScript("window").asInstanceOf[netscape.javascript.JSObject].eval("vuiInit.notify();")
            }*/
            
            

        }

      // org.w3c.dom.html.HTMLDOMImplementation
      /*   var factory = DocumentBuilderFactory.newInstance();
        
        factory.setAttribute("http://apache.org/xml/properties/dom/document-class-name",classOf[org.apache.html.dom.HTMLDocumentImpl].getCanonicalName) 
        
        var builder = factory.newDocumentBuilder();

        var newElt = builder.parse(new ByteArrayInputStream(newHTML.getBytes))

       //org.w3c.dom.html.
       
        //dom.getOwnerDocument.asInstanceOf[HTMLDocument].getDomConfig.

        // Create New Element
        println(s"New Element str: $newHTML")
        // dom.getOwnerDocument.getImplementation.
        var newDOM = dom.getOwnerDocument.importNode(newElt, true).asInstanceOf[DT]

        // Replace in parent
        var parent = dom.getParentNode
        parent.replaceChild(dom, newDOM)

        this.delegate = newDOM*/
    }
  }

  // Events
  //------------------
  /**
   * Bind the execution of the Closure to a call through embedded JS
   */
  override def onClickFork(cl: => Any) = {

    var clid = this.getRootParent.asInstanceOf[StandaloneHTML].registerClosure {
      _ => cl
      //
    }
    this("onclick" -> s"base.call('$clid',this)")
    //super.onClickFork(cl)

    /*action = Some({() => cl})
    println(s"In ONCLICK FORK")
    this("id" -> "test")
    //this.click
    this("onclick" -> s"""base.call('${this.hashCode().toHexString}')""")
    
    //this("onclick" -> s"""top.test()""")
    
    var h = this.getRootParent.asInstanceOf[StandaloneHTML]
    
    //h.actions = h.actions + (this.hashCode().toHexString -> { () => cl } )*/

  }

  override def onChanged(cl: => Any) = {
    var clid = this.getRootParent.asInstanceOf[StandaloneHTML].registerClosure {
      _ => cl
      //
    }
    this("onchange" -> s"base.call('$clid',this)")
  }

  override def onLoad(cl: Any => Any) {
    /*var clid = this.getRootParent.asInstanceOf[StandaloneHTML].registerClosure {
      o =>
        cl(o)

      //
    }*/
    println("LINKING onLoad to BOUND")
    this.on("bound") {
      cl(this)
    }
    //this("onload" -> s"base.call('$clid',this)")
  }

  override def onKeyTyped(cl: Char => Unit) = {

    // Register Closure
    var id = this.getRootParent.asInstanceOf[StandaloneHTML].registerClosure {
      case x if (x.toString() == "") =>
      case x => cl(x.toString.charAt(0))
      //
    }
    this("onkeyup" -> s"base.call('$id',this.value)")
    //this("onkeyup" -> s"base.call('$id',this.value.charAt(this.value.length-1))")
    //this.value.charAt(this.value.length-1)

  }

  // Bind
  //--------------
  this.on("parent.changed") {

    var bindCL = this.getRootParent.asInstanceOf[StandaloneHTML].registerClosure {
      obj =>
        println(s"Bound: ${this.htmlNodeName} to $obj -> ${obj.getClass().getCanonicalName}")
        this.delegate = obj.asInstanceOf[DT]
        this.@->("bound")
    }
    this("vui-bind" -> bindCL)
    /*this.onLoad {
      obj =>
        println(s"Loaded: ${this.htmlNodeName} to $obj -> ${obj.getClass().getCanonicalName}")
    }*/
  }

}

/*
trait StandaloneHTMLUIBuilder extends HtmlTreeBuilder {

  override def createDiv = new Div with StandaloneHTMLNode[org.w3c.dom.html.HTMLElement] with RebuildableNode[org.w3c.dom.html.HTMLElement]

  override def createA = {

    var aelt = new A("-", "-") with StandaloneHTMLNode[org.w3c.dom.html.HTMLElement]
    aelt
  }

  override def createInputText(name: String) = new StandaloneInputText(name)

  override def button(text: String)(cl: => Any) = {

    var b = new StandaloneButton
    b.textContent = text

    this.switchToNode(b, {
      //attribute(("id","test"))
      cl
    })
    b

  }

  override def html(cl: => Any): Html = {

    var h = new StandaloneHTML
    switchToNode(h, cl)
    h

  }

  override def head(cl: => Any): Head = {
    var h = super.head(cl)
    onNode(h) {
      script("text/javascript") {"""
      
var vuiInit = $.Deferred();

      """}
    }
    h
  }
  
  override def body(cl: => Any): Body = {
    var b = new StandaloneBody
    switchToNode(b, {
      
      script("text/javascript") {"""
      
//var vuiInit = $$.Deferred();

      """}
   
    
      cl

      // Add Script with binding call
      //----------------
      script("text/javascript") {
        s"""


vuiInit.progress(function() {

  
  console.info("Trying to bind ");
  $$("[vui-bind]").each(function(index,elt) {
      console.info("Binding "+elt+" to "+$$(elt).attr('vui-bind'));
      
      base.call($$(elt).attr('vui-bind'),elt);

      $$(elt).removeAttr('vui-bind');
  });

});

function vuiCall(id,elt) {

  console.info("Calling id "+id+" on "+elt+" with base: "+base);
  base.call(id,elt)

}
"""
      }
    })
    b
  }

  // Form Inputs
  //------------------------
  override def inputText(name: String)(cl: => Any) = switchToNode(new StandaloneInputText(name).asInstanceOf[InputText], cl)

  // DOM Events
  //--------------------
  def onLoad[IT <: HTMLNode[_ <: org.w3c.dom.html.HTMLElement]](cl: IT => Any) = {
    val target = currentNode
    target.onLoad {
      obj => cl(target.asInstanceOf[IT])
    }
  }

}

class StandaloneHTML extends Html with StandaloneHTMLNode[org.w3c.dom.html.HTMLElement] with JSEngineReference {

  var actions = Map[String, Any => Any]()

  // Register to JS Engine

  def registerClosure(cl: Any => Any) = {

    // Id 
    var id = cl.hashCode().toHexString
    actions = actions + (id -> cl)

    id
  }

  def call(code: String, param: AnyRef) = {

    println(s"Calling for action $code, contained:  ${actions.contains(code)}")
    //println(s"Calling with ${param.toString()}")
    actions.get(code) match {
      case Some(action) => action(param)
      case None => throw new RuntimeException("Cannot find action to call");
    }

  }
 /* def call(code: String) = {

    //println(s"Calling ")
    actions.get(code) match {
      case Some(action) => action()
      case None => throw new RuntimeException("Cannot find action to call");
    }

  }
  def test(int: java.lang.Integer) = {
    println(s"Calling")
  }*/

  def drop(e: netscape.javascript.JSObject) = {
    println(s"Calling Drop: " + e.getMember("dataTransfer").asInstanceOf[netscape.javascript.JSObject].call("getData", "text"))
  }
}

class StandaloneBody extends Body with StandaloneHTMLNode[org.w3c.dom.html.HTMLElement] {

}
class StandaloneButton extends Button {

  var action: Option[() => Any] = None

  /**
   * Bind the execution of the Closure to a call through embedded JS
   */
  override def onClickFork(cl: => Any) = {
    //super.onClickFork(cl)

    action = Some({ () => cl })
    println(s"In ONCLICK FORK")
    this("id" -> "test")
    //this.click
    this("onclick" -> s"""base.call('${this.hashCode().toHexString}')""")

    //this("onclick" -> s"""top.test()""")

    var h = this.getRootParent.asInstanceOf[StandaloneHTML]

    //h.actions = h.actions + (this.hashCode().toHexString -> { () => cl } )

  }

  override def click = {
    action match {
      case Some(act) => act()
      case None =>
    }
  }

}

// Form Inputs
//---------------------
class StandaloneInputText(lname: String) extends InputText(lname) with StandaloneHTMLNode[org.w3c.dom.html.HTMLInputElement] {

}*/

*/