

package com.idyria.osi.vui.html

import scala.reflect.ClassTag

class HTMLNode[HT <: org.w3c.dom.html.HTMLElement, +Self](var nodeName: String) extends com.idyria.osi.vui.core.definitions.VUIComponent[HT, Self] {

  this: Self =>

  // Selector 
  //----------------
  def select(selector: String): scala.Option[HTMLNode[HT, HTMLNode[HT, _]]] = {
    None
  }

  // Class Fields 
  //---------------------

  // Init Section 
  //----------------------

  // Methods
  //------------------

  // Imported Content 
  //----------------------
  // Imported from E:/Common/Projects/git/vui2/vui2-html/src/gen/HTMLNode.body.scala
  // DOM Base
  //--------------------
  var delegate: HT = _
  def base = delegate

  // Parameters
  // var name: String = null

  override def clear = {
    super.clear
    //println(s"Delegate is :$this.delegate")
    this.attributes = Map[String, Any]()
    this.delegate match {
      case null =>
      case d =>
        d.setTextContent("")
        var cn = d.getChildNodes
        (0 until cn.getLength) foreach {
          i =>
            d.removeChild(cn.item(i))
        }
    }
  }

  def clearChildren = {
    super.clear
    //println(s"Delegate is :$this.delegate")
    this.delegate match {
      case null =>
      case d =>
        d.setTextContent("")
        var cn = d.getChildNodes
        (0 until cn.getLength) foreach {
          i =>
            d.removeChild(cn.item(i))
        }
    }
  }

  //----------------------
  // General Control
  //----------------------

  //-- Enable/Disable

  /**
   * To be overriden if the component can be disabled
   */
  def disable: Unit = {

  }

  /**
   *  To be overriden if the component can be enabled
   */
  def enable = {

  }

  //----------------------
  // Positioning
  //----------------------
  /**
   * Set the position of the component
   */
  def setPosition(x: Int, y: Int) = {

  }

  /**
   * Get the position of the component
   */
  def getPosition: Pair[Int, Int] = {
    (0, 0)
  }

  def size(width: Int, height: Int) = {
    apply("style" -> ("" + attributes.getOrElse("style", "") + s";width:${width}px;height:${height}px"))

  }

  // SGGroup Implementation
  //-------------------

  // var childrenSeq = Seq[HTMLNode]()

  /**
   * Clear Children
   */
  /* def clear: Unit = {
    this.childrenSeq = this.childrenSeq.filter(_ => true)
  }*/

  /**
   * Remove one child
   */
  /*override def removeChild(n: SGNode[Any]) = {
    this.children.contains(n) match {
      case true  => this.childrenSeq = this.children diff Seq(n)
      case false =>
    }
  }*/

  /**
   * Returns all children
   */
  //def children = this.childrenSeq

  /**
   * Not doing anything
   */
  def revalidate: Unit = {}

  /**
   * Children nodes are saved locally
   */
  /* override def node[NT <: SGNode[Any]](content: NT): NT = {

    childrenSeq = childrenSeq :+ content.asInstanceOf[HTMLNode]
    super.node(content)
  }*/

  def :::[NT <: HTMLNode[HT, _]](parent: NT): HTMLNode[HT, _] = {

    parent.node(this)

    this
  }

  /**
   * Placeholder mechanism
   */
  def waitFor(id: String) = {

    this.onWith(id) {
      content: Any =>

      // println("*** Adding content: "+id+" of type: "+content.getClass)

      /* content match {
          case node: VUISGNode[_,_] => this <= node.asInstanceOf[VUISGNode[HT,_]]
          case nodes if (nodes.isInstanceOf[Iterable[_]]) => nodes.asInstanceOf[Iterable[_]].foreach { n => this <= n.asInstanceOf[VUISGNode[HT,_]] }
          case _ =>
        }*/
    }

    /*this.onWith(id) {
      node: SGNode[Any] => this <= node
    }*/
  }

  def place(id: String)(cl: HTMLNode[HT, _]) = {
    //this.@->(id, cl)
  }

  // Tree Location
  //-----------------

  /**
   * Remove from parent if any
   */
  def orphan = {
    this.parent match {
      case None =>
      case Some(p) =>p.removeChild(this)
    }
    this

  }

  // HTML Stuff
  //-----------------

  /**
   * Node name used to produce html
   */
  //var nodeName: String

  var textContent: String = ""

  var attributes = Map[String, Any]()

  // ID
  //------------
  def getId: String = {
    attributes.get("id") match {
      case Some(f) => f.toString()
      case other => 
        //-- generate
        this.setId(this.hashCode().toString())
    }
  }
  
  def setId(str:String) =  {
    this("id"->str)
    str
  }

  // Attributes
  //-----------------
  def apply(attr: (String, Any)) = {

    this.attributes.contains(attr._1) match {
      case false =>

        this.attributes = attributes + attr

      case true =>
        this.attributes = attributes - attr._1
        this.attributes = attributes + attr
    }

  }
  def +@(attr: (String, Any)) = {

    this(attr)
  }

  def ++@(attr: (String, Any)) = {

    attributeAppend((attr._1, attr._2.toString))
  }

  def attributeAppend(attr: (String, String)) = {

    this.attributes.get(attr._1) match {
      case None =>

        this.attributes = attributes + attr

      case Some(actualValue) =>

        this.attributes = attributes + (attr._1 -> s"$actualValue ${attr._2}")
    }

  }

  def attribute(name: String) = {
    this.attributes.get(name) match {
      case None =>

        ""

      case Some(actualValue) =>

        actualValue.toString
    }
  }

  def attributeOption(name: String) = {
    this.attributes.get(name)
  }
  
  def hasAttribute(name:String) = attributes.contains(name)

  // Classes
  //----------------
  def removeClass(name:String) = {
    attributeOption("class") match {
      case Some(classValue) =>
        +@("class" -> classValue.toString.replace(name,""))
      case None => 
    }
    this
  }
  
  def hasClass(name:String) = {
    attributeOption("class") match {
      case Some(classes) if (classes.toString().split(" ").find(_==name).isDefined) => true
      case other => false
    }
  }
  
  // Data
  //----------------
  def getDataOfType[T](str:String)(implicit dt:ClassTag[T]) = attributes.get("data-"+str) match {
    case Some(found) if (dt.runtimeClass.isInstance(found))=>Some(found.asInstanceOf[T])
    case other => None
  }
  
  def onDataOfType[T](str:String)(cl: T => Any)(implicit dt:ClassTag[T]) = getDataOfType[T](str) match {
    case Some(found) => cl(found)
    case other => 
  }
  
  /**
   * Left sid assignment of string adds classes
   */
  def ::(cl: String): Self = {
    
    // Find # to define ID
    var specString = """#([\w-_\.]+)""".r.findFirstMatchIn(cl) match {
      case Some(m) =>
        this.++@("id" -> m.group(1))
        cl.replace(m.group(0), "")
      case None => cl
    }
    
    // Split string spec and extract "@" for attributes
    //--------------
    specString.split(" ").filter(_.length()>0).groupBy {
      case str if(str(0)=='@') => '@'
      case other => '.'
    }.foreach {
      case ('@',values) => 
        values.foreach {
          v => 
            v.drop(1).split("=").filter(_.isEmpty()==false) match {
              case splitted if (splitted.size==0) => 
              case splitted if (splitted.size==1) => +@(splitted(0) -> "true")
              case splitted =>  +@(splitted(0) -> splitted(1).stripPrefix("\"").stripSuffix("\""))
            }
        }
      case ('.',values) => 
        values.foreach {
          v => 
            ++@("class",v)
            
        }
      case other => 
    }
    
   
    this.asInstanceOf[Self]
  }

  // Left assign of ID
  //-----------------------
  def #:(id: String): Self = {
    this("id" -> id)
    this.id = id
    this.asInstanceOf[Self]
  }

  // Left Attributes assignment
  //----------------
  def @:(attr: String): Self = {

    attr.split(" ").map(_.split("=")).foreach {
      case one if (one.size == 1) => this(one(0) -> "")
      case two => this(two(0) -> two(1).replace("\"", "").replace("'", ""))
    }
    this.asInstanceOf[Self]
  }
  def @:(attrs: (String, Any)*): Self = {
    attrs.foreach {
      attr => this(attr)
    }
    this.asInstanceOf[Self]
  }

  // Render
  //----------------

  /**
   * Renders HTML Node structure as String
   */
  override def toString: String = {

    // Prepare attributes
    //-------------------------
    var attrs = attributes.size match {
      case 0 => "" case _ => attributes.map {

        // If Value contains " then use '' as outside delimiter
        case (name, value) if (value.toString.contains('"')) =>
          s"""${name}='${value}'""".trim
        case t =>
          s"""${t._1}="${t._2}"""".trim

      }.mkString(" ", " ", "")
    }

    /*var indentString = this.indentCount match {
      case 0 => List("")
      case indentCount => for (i <- 1 to indentCount) yield "    "
    }*/
    var indentString = ""

    s"""${indentString.mkString}<$nodeName$attrs>${textContent}${indentString.mkString}${this.children.map(_.toString).mkString("\n\n")}${indentString.mkString}</$nodeName>"""
  }

  // DOM Events
  //---------------------
  def onChanged(cl: => Any): Unit = {

  }

  def onLoad(cl: Any => Any): Unit = {

  }

  def doClick = {
  }

  // Content Update API
  //--------------------
  def updateContent = {
  }

}


                    
