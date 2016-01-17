/**
   * Bind the execution of the Closure to a call through embedded JS
   */
  override def onClick(cl: => Any) = {

    var clid = this.getRootParent.asInstanceOf[StandaloneHtml[BT,_]].registerClosure {
      _ => cl
      //
    }
    this("onclick" -> s"base.call('$clid',this)")

  }
  
  import org.w3c.dom.html.HTMLElement
  
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
    var base = this.getRootParent.asInstanceOf[StandaloneHtml[HTMLElement,_]]
     
    //-- Get Closure
    base.getClosure(clId)match {
      case None => 
      case Some(cl) => 
        //-- Empty Current and Run Closure
        this.clear
        cl(null)
        //println(s"Content Regenerated on Scala Side")
        
        //-- Get DOM (content definition forces bind)
        var dom = this.delegate
        
        //-- Now Replace using JQuery
        // Get string
        var newHTML = this.toString()
        var newHTMLCleaned = newHTML.trim.replace("\"","\\\"").replace("\r\n", "").replace("\n", "").replaceAll("\\s+"," ").replaceAll("(>|<) (<|>)","$1$2")
       //println(s"Parsing: ${newHTMLCleaned}");
        
        // Parse using JS
        base.engine match {
          case None => throw new RuntimeException("Cannot Rebuild HTML, no engine set on parent HTML")
          case Some(engine) =>

            // wrapper.innerHTML="${newHTML.trim.replace("\"","'").replace("\n", "")}"
//            wrapper.innerHTML="<div>\\(ax^2 + bx + c = 0\\)</div>"
            var elt = engine.executeScript(s"""

//console.log('parsing');
var res = $$.parseHTML("${newHTMLCleaned}");
var nodes = []
$$.each( res, function( i, el ) {
 //console.log('parsing done');
nodes[i] = el
});
nodes[0];
//$$.first(res);

            """)
            
            /*elt = elt match {
              case text : Text => 
                println(s"First Child is text, trying next -> "+text.getNextSibling)
                text.getNextSibling
              case elt => elt
            }
            println(s"Parsed: $elt -> ")*/
           
           // println(s"Parsed FS: ${elt.asInstanceOf[HTMLElement].getFirstChild.getNextSibling}")
            
            //-- Import New DOM in current Document
            var newDOM = dom.getOwnerDocument.importNode(elt.asInstanceOf[org.w3c.dom.Node], true).asInstanceOf[HTMLElement]
            
            //-- Replace Current Content with newDom's content
           /* var children = dom.getChildNodes
            (0 until children.getLength) foreach {
              i => 
                dom.removeChild(children.item(i))
            }
            var newChildren = newDOM.getChildNodes
            (0 until newChildren.getLength) foreach {
              i => 
                dom.appendChild(newChildren.item(i))
            }*/
            var parent = dom.getParentNode
           parent.replaceChild(newDOM,dom)
        }
    }
    
    
    //var contentNode
    
  }