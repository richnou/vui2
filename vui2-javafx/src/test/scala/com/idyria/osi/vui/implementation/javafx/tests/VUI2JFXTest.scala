package com.idyria.osi.vui.implementation.javafx.tests

import com.idyria.osi.vui.core.definitions.VUIBuilder
import com.idyria.osi.vui.html.standlone.StandaloneHTMLViewCompiler
import com.idyria.osi.vui.implementation.javafx.JavaFXRun
import com.idyria.osi.vui.html.basic.BasicHTMLViewCompiler

object VUI2JFXTest extends App with VUIBuilder {
  
  println(s"Started JFX Test")
  //var f = frame
  
  var view = BasicHTMLViewCompiler.createView(classOf[TryHTMLView])
  
  JavaFXRun.onJavaFX {
    
    var f = frame 
    f {
      f => 
        f.title =" JFX Test"
        f.visible = (true)
        
        f <= webbrowser {
          br => 
            
            /*
            br.content = """<html>
<head></head>
  <body><h1>Test</h1></body>
</html>
"""*/
            br.view = view
        }
    }
    
    
    /*{
      f =>
        f.title =" JFX Test"
        f.visible = (true)
    }*/
    
    
    
  }
  
}