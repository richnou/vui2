package com.idyria.osi.vui.implementation.javafx.tests

import com.idyria.osi.vui.html.standlone.StandaloneHTMLView
import com.idyria.osi.vui.html.standalone.StandaloneHtmlBuilder
import com.idyria.osi.vui.html.standlone.DefaultStandaloneHTMLBuilder
import com.idyria.osi.vui.html.standalone.StandaloneBasicHTMLBuilderTrait
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.basic.BasicHTMLView
import com.idyria.osi.vui.html.lib.semanticui.SemanticUIBuilder



class TryHTMLView extends BasicHTMLView with StandaloneBasicHTMLBuilderTrait[HTMLElement] with SemanticUIBuilder{
  
  this.content {
    html {
      head {
        
      }
      
      body {
        currentNode.textContent ="TEst 2"
        
        h1 {
          currentNode.textContent = "Hello"
        }
      }
      
    }
  }
  
}