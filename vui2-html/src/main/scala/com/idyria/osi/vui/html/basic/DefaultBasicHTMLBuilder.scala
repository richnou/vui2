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
import com.idyria.osi.vui.html.Table
import com.idyria.osi.vui.html.Tbody
import com.idyria.osi.vui.html.Th

trait DefaultBasicHTMLBuilder extends BasicHTMLBuilderTrait[HTMLElement] {

  // Parts
  //---------------
  // Placeholder
  //--------------
  var placesMap = Map[String, () => HTMLNode[HTMLElement, _]]()

  def placePart(id: String) = {
    placesMap.get(id) match {
      case Some(cl) =>
        Some(switchToNode(cl(), {}))

      case None =>
        None
      //throw new RuntimeException("Cannot place part: " + id + " because it hasn't been defined")
    }
  }

  def definePart(id: String)(cl: => HTMLNode[HTMLElement, _]) {
    placesMap = placesMap + (id -> { () => cl })
  }

  // Sub View Placement
  //----------------------
  def addView[T <: BasicHTMLView](v: T) = {

    val r = v.rerender
    r.detach
    add(r)
    r
  }

  // Structure
  //---------------

  /**
   * Places a <meta charset="utf-8" />  tag
   */
  def utf8Meta = {
    importHTML(<meta charset="utf-8"/>)
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
  def attributeIf(test: Boolean)(attributeName: String, attributeValue: String = "") = {
    if (test) {
      +@(attributeName -> attributeValue)
    }

  }

  /**
   * Adde data atribute
   */
  def data(nameValue: (String, Any)) = {
    +@("data-" + nameValue._1 -> nameValue._2)
  }

  // Text
  //--------------
  def span(str: String): Span[HTMLElement, Span[HTMLElement, _]] = {
    span {
      textContent(str)
    }
  }
  def p(str: String): P[HTMLElement, P[HTMLElement, _]] = {
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
        (inputElement.attributeOption("id"), labelElement.attributeOption("id")) match {
          case (Some(id), None) =>
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

  /**
   * Makes select with options based on tuples
   * (value -> Display Text)
   */
  def selectOptions(optionsInput: List[(Any, Any)])(cl: => Any) = {
    select {

      options(optionsInput)

      cl
    }
  }

  def options(optionsInput: List[(Any, Any)]) = {
    optionsInput.foreach {
      case (v, t) => option(v.toString)(text(t.toString))
    }
  }

  // Value Range definition
  //----------------------
  def maxValue(str: Number) = {
    +@("max" -> str)
  }
  def minValue(str: Number) = {
    +@("min" -> str)
  }
  def stepValue(v: Double) = {
    +@("step" -> v.toString())
  }

  def isSelected = {
    +@("selected" -> true)
  }

  def isChecked = {
    +@("checked" -> true)
  }

  def fieldName(str: String) = {
    +@("name" -> str)
  }
  def fieldNameId(str: String) = {
    +@("name" -> str)
    +@("id" -> str)
  }

  def fieldPlaceholder(text: String) = {
    +@("placeHolder" -> text)
  }
  
  def fieldPlaceHolderOrValue(pl:String,v:Option[Any]) : Unit = {
    v match {
      case Some(v) => 
        +@("value" -> v)
      case None => 
        fieldPlaceholder(pl)
    }
  }
  
  def fieldPlaceHolderOrValue[T](pl:String,source:Option[T],cl: T => Any) : Unit = {
    source match {
      case Some(v) => 
        +@("value" -> cl(v))
      case None => 
        fieldPlaceholder(pl)
    }
  }

  def fieldNameAndPlaceholder(name: String, text: String) = {
    fieldName(name)
    fieldPlaceholder(text)

  }

  def hiddenInput(name: String)(cl: => Any) = {
    input {
      +@("type" -> "hidden")
      fieldName(name)
      cl
    }
  }

  // Import XML Parsed Stuff
  //----------
  def importHTML(xml: Elem) = {

    switchToNode(new XMLHTMLNode[HTMLElement, XMLHTMLNode[HTMLElement, _]](xml), {})
  }

  def $(xml: Elem)(cl: => Any): XMLHTMLNode[HTMLElement, XMLHTMLNode[HTMLElement, _]] = {
    switchToNode(new XMLHTMLNode[HTMLElement, XMLHTMLNode[HTMLElement, _]](xml), cl)
  }

  /**
   * Selector
   */
  def $(selector: String): Option[HTMLNode[HTMLElement, HTMLNode[_, _]]] = {
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
  def text(str: String) = {
    var tn = new TextNode[HTMLElement, TextNode[HTMLElement, _]](str)
    switchToNode(tn, {})
    tn
  }

  def tspan(str: String) = {
    span(text(str))
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

 override def tr(cl: => Any) = {
    currentNode match {
      case tr: Tr[HTMLElement, Tr[HTMLElement, _]] =>

        val rs = super.tr {
          cl
        }
        rs.detach
        currentNode.parent.get.addChild(rs)
        rs

      case other =>
        super.tr {
          cl
        }
    }
  }

  def rtd(cl: => Unit): Td[HTMLElement, Td[HTMLElement, _]] = {
    td("") {
      cl
    }
  }

  def rth(cl: => Unit): Th[HTMLElement, Th[HTMLElement, _]] = {
    th("") {
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

  def thead(headers: List[String]): Thead[HTMLElement, Thead[HTMLElement, _]] = {
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

  def trvalues(values: Any*): Tr[HTMLElement, Tr[HTMLElement, _]] = {

    tr {
      values.foreach {
        case v: HTMLNode[_, _] =>
          v.detach
          switchToNode(v.asInstanceOf[HTMLNode[HTMLElement, _]], {})

        case null =>
        case v => td(v.toString()) {

        }

      }
    }

  }

  /**
   * Same as trvalues but for header
   */
  def trhvalues(values: Any*): Tr[HTMLElement, Tr[HTMLElement, _]] = {

    tr {
      values.foreach {
        case v: HTMLNode[_, _] =>
          v.detach
          switchToNode(v.asInstanceOf[HTMLNode[HTMLElement, _]], {})

        case null =>
        case v => th(v.toString()) {

        }

      }
    }

  }

  def tbodyTrLoop[V](objs: Iterable[V])(cl: V => Any) = {
    tbody {
      trLoop[V](objs)(cl)
    }
  }
  def trLoop[V](objs: Iterable[V])(cl: V => Any) = objs.foreach {
    v =>
      tr {
        cl(v)
      }
  }

  /**
   * Creates tbody, loop on objects, if the objects list is empty, add a text line spanning all columns
   */
  def tbodyTrLoopWithDefaultLine[V](default: String)(objs: Iterable[V])(cl: V => Any) = objs.size match {
    case 0 =>
      tbody {
        trtd(default) {
          +@("colspan" -> tableColumnsCount)
        }
      }
    case other =>

      tbodyTrLoop(objs) {
        r => cl(r)
      }
  }

  /**
   * For now call inside table
   */
  def tableColumnsCount = {

    // Look for table
    val table = currentNode match {
      case t: Table[HTMLElement, _] => Some(t)
      case other                    => other.findParentOfType[Table[HTMLElement, _]]

    }

    // Search for actual Colspan required
    val span = table match {
      case Some(t) if (t.children.find(n => n.isInstanceOf[Thead[_, _]]).isDefined) =>

        val thead = t.children.collectFirst { case tr: Thead[_, _] => tr }.get
        thead.children.collectFirst { case tr: Tr[_, _] => tr } match {
          case None =>
            //val thead = t.children.collectFirst { case tr: Thead[_, _] => tr }
            0

          case Some(tr) =>
            tr.children.collect { case td: Th[_, _] => td }.map {
              case td if (td.hasAttribute("colspan")) => td.attribute("colspan").toInt
              case td                                 => 1
            }.sum
        }
      case Some(t) if (t.children.find(n => n.isInstanceOf[Tbody[_, _]]).isDefined) =>

        val tbody = t.children.collectFirst { case tr: Tbody[_, _] => tr }.get
        tbody.children.collectFirst { case tr: Tr[_, _] => tr } match {
          case None =>
            //val thead = t.children.collectFirst { case tr: Thead[_, _] => tr }
            0

          case Some(tr) =>
            tr.children.collect { case td: Td[_, _] => td }.map {
              case td if (td.hasAttribute("colspan")) => td.attribute("colspan").toInt
              case td                                 => 1
            }.sum
        }

      case other => 0
    }

    span

  }

  /**
   * Makes tfoot with one tr and a full spanning td
   */
  def tfootTrTh(cl: => Any) = {

    // Search for actual Colspan required
    val span = currentNode match {
      case t: Table[_, _] if (t.children.find(n => n.isInstanceOf[Tbody[_, _]]).isDefined) =>

        val tbody = t.children.collectFirst { case tr: Tbody[_, _] => tr }.get
        tbody.children.collectFirst { case tr: Tr[_, _] => tr } match {
          case None =>
            //val thead = t.children.collectFirst { case tr: Thead[_, _] => tr }
            0

          case Some(tr) =>
            tr.children.collect { case td: Td[_, _] => td }.map {
              case td if (td.hasAttribute("colspan")) => td.attribute("colspan").toInt
              case td                                 => 1
            }.sum
        }

      case other => 0
    }
    tfoot {
      tr {
        rth {
          colspan(span)
          cl
        }
      }
    }

  }

  /**
   * Create a tr with one td and cl int td
   */
  def trtd(text: String)(cl: => Any) = {
    tr {
      td(text) {
        cl
      }
    }
  }

  def colspan(v: Int) = {
    +@("colspan" -> v)
  }
  def rowspan(v: Int) = {
    +@("rowspan" -> v)
  }

  // Styles
  //-----------
  def cssForceBlockDisplay = {

    ++@("style" -> "display:block;")
  }
}

object DefaultBasicHTMLBuilder {
  implicit class NoDoubleSlash(val underlying: String) extends AnyVal {
    def noDoubleSlash: String = underlying.replaceAll("//+", "/")
  }

  //implicit def c(u:String) = new NoDoubleSlash(u)

}