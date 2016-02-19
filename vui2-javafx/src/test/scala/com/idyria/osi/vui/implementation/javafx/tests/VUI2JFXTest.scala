package com.idyria.osi.vui.implementation.javafx.tests

import com.idyria.osi.vui.core.definitions.VUIBuilder
import com.idyria.osi.vui.html.standalone.StandaloneHTMLViewCompiler
import com.idyria.osi.vui.implementation.javafx.JavaFXRun
import com.idyria.osi.vui.html.basic.BasicHTMLViewCompiler
import javafx.scene.web.WebView

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
            //br.view = view
            br.base.asInstanceOf[WebView].getEngine.load("http://www.edankwan.com/experiments/smashing-mega-scene/")
        }
    }
    
    
    /*{
      f =>
        f.title =" JFX Test"
        f.visible = (true)
    }*/
    
    
    
  }
  
}