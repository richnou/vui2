package com.idyria.osi.vui.html.lib.semanticui

import com.idyria.osi.vui.html.basic.BasicHTMLBuilderTrait
import org.w3c.dom.html.HTMLElement
import java.net.URL
import java.net.URI

trait SemanticUIBuilder extends BasicHTMLBuilderTrait[HTMLElement] {

  // Config
  //---------------
  var semanticUIVersion = "2.1.4"
  
  // Module usage
  //----------------------

  // Building
  //--------------------
  override def html(cl: => Any) = {
    super.html {

      head {
        script(new URI("https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js")) {

        }

        /*script(getClass.getClassLoader.getResource("semantic/dist/semantic.min.js"), "text/javascript") {

        }

        stylesheet(getClass.getClassLoader.getResource("semantic/dist/semantic.min.css")) {

        }*/
      }

      cl

    }
  }
  /* override def html(cl: => Any): Html = {
    super.html {

      head {
        script {
          //attribute("src" -> "https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js")
          attribute("src" -> getClass.getClassLoader.getResource("js/jquery-2.1.4.min.js").toString())
          //attribute("src" -> new File("src/main/resources/js/jquery-1.11.3.min.js.js").toURI().toURL().toString())
        }

        stylesheet(getClass.getClassLoader.getResource("semantic/dist/semantic.min.css").toString())
        script {
          attribute("src" -> getClass.getClassLoader.getResource("semantic/dist/semantic.min.js").toString())
        }

        stylesheet(getClass.getClassLoader.getResource("semantic/dist/semantic.min.css").toString())
      }

      cl

      // Update head
      /*var headNode = currentNode.children.find { n => n.isInstanceOf[Head] } match {
        case Some(head) => head
        case None =>
      }
      onNode(headNode.asInstanceOf[HTMLNode]) {

      }*/
    }
  }*/

  // Language
  //---------------
  /*class ApplyClasses(cl: String) extends HtmlTreeBuilder {

    def apply(n:HTMLNode) : HTMLNode = {
      
      onNode(n) {
        addClasses(cl)
      }
      n
      
    }
    
  }
  
  implicit def convertStringToApplyClasses(str:String) : ApplyClasses = new ApplyClasses(str)*/

  // Tabs
  //------------------
  /*def tabpane = {
    
    // Create Container Div
    //---------------------------
    var tp = new Div with VUITabPane[org.w3c.dom.html.HTMLElement] {
      
      "ui tabpane" :: this
      
      // Header
      //--------------
      var headerDiv = new Div {
        "ui top attached tabular menu" :: this
      }
      this.addChild(headerDiv)
      
     
      
      
      // Tab Add methods
      //-------------------
      def addTab[NT <: com.idyria.osi.vui.core.components.scenegraph.SGNode[org.w3c.dom.html.HTMLElement]](title: String)(content: NT): com.idyria.osi.vui.core.components.containers.VUITab[org.w3c.dom.html.HTMLElement] = {
        
       
        
        // Create Tab Div
        //----------------------
        var tabdiv = new Div with VUITab[org.w3c.dom.html.HTMLElement] {
          
          "ui  bottom attached tab segment" :: this
          
          def setClosable(b:Boolean) = {
            
          }
          
          override def apply(cl: VUITab[org.w3c.dom.html.HTMLElement] => Unit) : VUITab[org.w3c.dom.html.HTMLElement] = {
            switchToNode(this, {
              cl(this)
            })
            this
          }
        }

        // Add to container
        this.addChild(tabdiv)
        
         // Add Content to tab
        tabdiv <= content
        
        // Create ID From Title
        //-------------------------
        var id = title.toLowerCase().replace(" ","_")
        tabdiv("data-tab" -> id)
        
        // Create Header Div (Header containre is first child of "this")
        //----------------------
        onNode(headerDiv) {
          "ui item" :: div {
            
            //-- First one is active
            if (headerDiv.children.size==1) {
              classes("active")
              "active" :: tabdiv
            }
           
            //-- Add Id 
            attribute("data-tab" -> id)
            
            //-- Add Header as text 
            text(title)
            
            
            
          }
        }
        
        tabdiv
        
      }
      
      def node[NT <: com.idyria.osi.vui.core.components.scenegraph.SGNode[org.w3c.dom.html.HTMLElement]](title: String)(content: NT): NT = this.addTab(title)(content).asInstanceOf[NT]
    }
    
    // Configure Tab container
    //"ui tabular menu" :: tp
    //---------------------------------
    switchToNode(tp, {
      
    })
    
    tp
  }
  */
}