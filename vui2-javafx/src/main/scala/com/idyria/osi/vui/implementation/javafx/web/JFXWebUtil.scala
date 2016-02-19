package com.idyria.osi.vui.implementation.javafx.web

import com.idyria.osi.vui.core.definitions.VUIBuilder
import com.idyria.osi.vui.core.definitions.VUIFrame
import com.idyria.osi.vui.implementation.javafx.JavaFXRun
import com.idyria.osi.vui.html.basic.BasicHTMLView
import com.idyria.osi.vui.implementation.javafx.JavaFXUtilsTrait

trait JFXWebUtil extends VUIBuilder with JavaFXUtilsTrait {
  
  
  def createBrowserUI  = {
    
    JavaFXRun.onJavaFX {
       var web = webbrowser 
      var f = frame {
        f => 
          
          f <= web
          
        
      }
       
       (f,web)
    }.get 
  }
  
  def createBrowserUI(view:BasicHTMLView) = {
    JavaFXRun.onJavaFX {
       var web = webbrowser 
      var f = frame {
        f => 
          
          f <= web
          web.view = view
          
        
      }
       
       (f,web)
    }.get 
  }
 
}