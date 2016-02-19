

package com.idyria.osi.vui.html.standalone


trait StandaloneHTMLNode[BT <: org.w3c.dom.html.HTMLElement, +Self] extends com.idyria.osi.vui.html.HTMLNode[BT, Self] {

  this: Self =>

  // Class Fields 
  //---------------------

  // Init Section 
  //----------------------

  // Methods
  //------------------

  // Imported Content 
  //----------------------
  // Imported from E:/Common/Projects/git/vui2/vui2-html/src/gen/StandaloneHTMLNode.body.scala

  import org.w3c.dom.html.HTMLElement

  /**
   * Bind the execution of the Closure to a call through embedded JS
   */
  override def onClick(cl: => Any) = {

    var clid = this.getRootParent.asInstanceOf[StandaloneHtml[BT, _]].registerClosure {
      _ => cl
      //
    }
    this("onclick" -> s"base.call('$clid',this)")

  }

  override def doClick = {

    this.attributes.get("onclick") match {
      case Some(value) =>

        //-- Get Base
        var base = this.getRootParent.asInstanceOf[StandaloneHtml[org.w3c.dom.html.HTMLElement, _]]
        base.engine match {
          case Some(jsengine) =>
            jsengine.executeScript(value.toString())
          case None =>
        }

      case None =>
    }
  }

  /**
   *
   */
  override def updateContent = {

    //-- Get VUI Content closure binding
    var clId = this.attribute("vui-content") match {
      case "" => sys.error(s"Cannot Update Content on Standalone Element with no vui-content attribute: ${getClass.getSimpleName}")
      case clId => clId.toString
    }

    //-- Get Base
    var base = this.getRootParent.asInstanceOf[StandaloneHtml[HTMLElement, _]]

    //-- Get Closure
    base.getClosure(clId) match {
      case None =>
      case Some(cl) =>
        //-- Get ID 
        var id = this.attributes("id").toString()

        //-- Get DOM (content definition forces bind)
        var dom = this.delegate

        //-- Empty Current node, from now on it is new and run closure
        this.clear
        cl(null)
        //println(s"Content Regenerated on Scala Side")

        //-- Now Replace using JQuery
        var newHTML = this.toString()
        var newHTMLCleaned = newHTML.trim
          .replace("\"", "\\\"")
          .replace("\r\n", "")
          .replace("\n", "")
          .replaceAll("\\s+", " ")
          .replaceAll("(>|<) (<|>)", "$1$2")
        // Preserve Backslashes
        // .replace("\\","\\\\")

        // newHTMLCleaned = newHTML.trim.replace("\"", "\\\"").replace("\r\n", "").replace("\n", "").replaceAll("\\s+", " ").replaceAll("(>|<) (<|>)", "$1$2")

        // println(s"Parsing: ${newHTMLCleaned}");

        // Parse using JS and replace
        base.engine match {
          case None => throw new RuntimeException("Cannot Rebuild HTML, no engine set on parent HTML")
          case Some(engine) =>

            var elt = engine.executeScript(s"""

//console.log('parsing');
var targetId = "$id"
var targetElt = $$("#"+targetId)
//console.log('Updating content of $id')
var res = $$.parseHTML("${newHTMLCleaned}",document,true )
targetElt.replaceWith(res);

//targetElt.html("${newHTMLCleaned}",document,true);

//console.log("Result HTML: "+$$(res).prop("outerHTML"));
/*return
var res = $$.parseHTML("${newHTMLCleaned}",document,true );
var nodes = []

$$.each( res, function( i, el ) {
  
//console.log('parsing done');
  nodes[i] = el
});
*/
//console.log("Result HTML: "+$$(nodes[0]).prop("outerHTML"))
//nodes[0];
//$$.first(res);

            """)

            //-- Import New DOM in current Document
            /*var newDOM = dom.getOwnerDocument.importNode(elt.asInstanceOf[org.w3c.dom.Node], true).asInstanceOf[HTMLElement]

            //-- Replace Current Content with newDom's content
            var parent = dom.getParentNode
            parent.replaceChild(newDOM, dom)*/

            // Execute the Scripts
            engine.executeScript("vuiUpdatedContent();");

        }
    }

    //var contentNode

  }

}


                    
