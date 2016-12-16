package com.idyria.osi.vui.html.xml

import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import scala.xml.Elem
import scala.xml.transform.RuleTransformer
import scala.xml.transform.RewriteRule
import scala.xml.Node
import scala.xml.Attribute
import scala.xml.Null

abstract class NodeRewriteRule(val targetNode:Node) extends RewriteRule

class XMLHTMLNode[HT <: org.w3c.dom.html.HTMLElement, +Self](val xml: Elem,val topwrapper : Option[XMLHTMLNode[HT,XMLHTMLNode[HT,_]]] = None) extends HTMLNode[HT, Self](nodeName = "wrapper") {
  this: Self =>

  var actualXML = xml

  /*def apply(cl: Self => Unit) : Unit = {
    cl(this)
  }*/

  // Transformations
  //-------------
  var transformer = new RuleTransformer(new RewriteRule {
    
    override def transform(n: Node): Seq[Node] = {
        
        var allrules = rules.filter {  rw => rw.targetNode==n }
        var nodeRes = Seq(n)
        
        allrules.foreach {
          r => 
            var res = r.transform(nodeRes(0))
            nodeRes = res(0)
        }
        
        nodeRes
    }
  })
  var rules = List[NodeRewriteRule]()
  

  // Attribute add
  //-----------
  override def +@(attr: (String, Any)) = {
      
    //println(s"Adding rule to change attribute")
    
    //-- Create Attribute change rule
    val rule = new NodeRewriteRule(xml) {
      
      override def transform(n: Node): Seq[Node] = {
        
       // println(s"Transforming node $n")
        n.asInstanceOf[Elem] % Attribute(null,attr._1,attr._2.toString,Null)
        
      }
      
    }
    
    //-- Add to top wrapper or local if we are top
    topwrapper match {
      case Some(top) => top.rules = top.rules :+ rule
      case None => this.rules = this.rules :+ rule
    }
    
    
    // xml.
  }

  // Click Helpers
  //-------------------
  override def select(selector: String): Option[HTMLNode[HT, HTMLNode[HT, _]]] = {

    selector match {
      case sel if (sel.startsWith("#")) =>

        (this.xml \\ "_").collectFirst {
          case node: Elem if (node.\@("id") == selector.drop(1)) =>
            node

        } match {
          case Some(element) =>
            Some(new XMLHTMLNode[HT,XMLHTMLNode[HT,_]](element,topwrapper = Some(this.asInstanceOf[XMLHTMLNode[HT,XMLHTMLNode[HT,_]]])))

          case None =>
            None
        }

      /*(this.xml \\ s"[id='${selector.drop(1)}']") match {
            case seq if (seq.length==0) => None
            case seq => Some(XMLHTMLNode(seq.head.asInstanceOf[Elem]))
          }
          */
      case _ => None
    }

    //this.xml.

  }

  override def toString: String = {

    // transform
    this.transformer.transform(xml).map {_.toString()}.mkString
    
   // return this.xml.toString()

  }
}

object XMLHTMLNode {

  def apply[HT <: org.w3c.dom.html.HTMLElement](elem: Elem) = new XMLHTMLNode[HT, XMLHTMLNode[HT, _]](elem)
}