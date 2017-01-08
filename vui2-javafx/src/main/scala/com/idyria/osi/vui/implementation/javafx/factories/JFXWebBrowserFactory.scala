

package com.idyria.osi.vui.implementation.javaf.factories

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.web.WebEngine
import javafx.scene.web.WebErrorEvent
import javafx.scene.web.WebView
import com.idyria.osi.vui.core.definitions.VUIWebBrowser
import com.idyria.osi.vui.core.view.AView
import com.idyria.osi.vui.html.standalone.StandaloneHtml
import com.idyria.osi.vui.implementation.javafx.JavaFXNodeDelegate
import com.idyria.osi.vui.implementation.javafx.JavaFXUtilsTrait
import com.idyria.osi.tea.io.TeaIOPipe
import com.idyria.osi.tea.io.TeaIOUtils
import java.io.File
import com.idyria.osi.vui.html.js.JSEngine

trait JSEngineReference {
  var engine: Option[WebEngine] = None
}

trait JFXWebBrowserFactory extends com.idyria.osi.vui.core.definitions.VUIWebBrowserFactory[javafx.scene.Node] with JavaFXUtilsTrait {

  // Class Fields 
  //---------------------

  // Methods
  //------------------
  override def createWebBrowser: com.idyria.osi.vui.core.definitions.VUIWebBrowser[javafx.scene.Node, com.idyria.osi.vui.core.definitions.VUIWebBrowser[javafx.scene.Node, _]] = {
    var w = new JavaFXNodeDelegate[WebView, VUIWebBrowser[WebView, _]](new javafx.scene.web.WebView()) with VUIWebBrowser[WebView, VUIWebBrowser[WebView, _]] {

      private val lc = this

      var targetView: AView[_,_] = null

      def log(str: String) = {
        println(str);
      }
      def error(str: String) = {
        println(str);
      }
      def reportError(message: String, script: String, line: Int, col: Int, errObj: netscape.javascript.JSObject) = {
        println(s"$message in $script:$line:$col : " + errObj);
      }
      // Errors
      //------------------
      this.base.getEngine.setOnError(new EventHandler[WebErrorEvent] {
        def handle(err: WebErrorEvent) = {
          println(s"Error Occured: " + err.getException.getMessage)
          err.getException.printStackTrace()
        }
      })

      // load Monitor
      //----------------------------
      var ioRedirectScript = """
console.log = function(message) {
           bridge.log(message);
}
console.info = function(message) {
           bridge.log(message);
}
console.error = function(message) {
           bridge.error(message.toString);
}
console.exception = function(message) {
           bridge.error(message.toString);
}
console.warn = function(message) {
           bridge.log(message);
}
window.onerror= function(message,script,line,column,errObj) {
          bridge.reportError(message,script,line,column,errObj);
          return true
}

;"""
      this.base.getEngine.getLoadWorker().stateProperty().addListener(
        new javafx.beans.value.ChangeListener[javafx.concurrent.Worker.State] {
          def changed(ov: javafx.beans.value.ObservableValue[_ <: javafx.concurrent.Worker.State], oldState: javafx.concurrent.Worker.State, newState: javafx.concurrent.Worker.State) = {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
              //stage.setTitle(webEngine.getLocation());
              println("Done LAODING")
              //Thread.sleep(500)
              onUIThread {
               
                var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]
                var document = base.getEngine.executeScript("document").asInstanceOf[netscape.javascript.JSObject]
                
                //window.setMember("bridge", lc);

                if (targetView != null && targetView.renderedNode != None) {
                  window.setMember("base", targetView.renderedNode.get)
                  //document.setMember("base", targetView.renderedNode.get)
                  targetView.renderedNode.get match {
                    case html : StandaloneHtml[_,_] => 
                      
                    case _ => 
                  }
                 // targetView.renderedNode.get.asInstanceOf[JSEngineReference].engine = Some(base.getEngine)
                }

               // base.getEngine.executeScript(ioRedirectScript);
                 base.getEngine.executeScript("""
window.requestAnimFrame = (function(){
    return  window.requestAnimationFrame   || 
        window.webkitRequestAnimationFrame || 
        window.mozRequestAnimationFrame    || 
        window.oRequestAnimationFrame      || 
        window.msRequestAnimationFrame     || 
        function(/* function */ callback, /* DOMElement */ element){
             window.setTimeout(callback, 1000 / 60);
        };
})();
""")
                 base.getEngine.executeScript("vuiStart();");
                 
            
              }
              // var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]

            }
          }
        });

      // I/O redirection
      //-------------------
      var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject].setMember("bridge", lc);
      base.getEngine.executeScript(ioRedirectScript);
      base.getEngine.impl_getDebugger().setEnabled(true)
      base.getEngine.impl_getDebugger().setMessageCallback(new javafx.util.Callback[String,Void] {
        def call(s:String) : Void = {
          
          println(s"Debug callback: $s").asInstanceOf[Void]
        }
      })
      
      // Loading/Reloading
      //------------------------
      def loadContent(str: String) = {
        base.getEngine.loadContent(str)
      }

      def loadURL(str: String) = {
        base.getEngine.load(str)
      }

      override def view_=(v: AView[_,_]) = onUIThread {
        targetView = v

        // Add Content
        var html = targetView.render
        println(s"Rendered HTML node (" + hashCode() + "): " + html.hashCode() + "//" + targetView.renderedNode.get.hashCode())
        var htmlString = html.toString()
        println(s"Rendered HTML node (" + hashCode() + "): " + html.hashCode() + "//" + targetView.renderedNode.get.hashCode())
        println(s"HTML: $htmlString")
         var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]
        // window.setMember("base", html)

        window.setMember("bridge", lc);
        this.base.getEngine.executeScript("""
console.log = function(message) {
           bridge.log(message);
}
console.info = function(message) {
           bridge.log(message);
}
console.error = function(message) {
           bridge.error(message.toString);
}
console.exception = function(message) {
           bridge.error(message.toString);
}
console.warn = function(message) {
           bridge.log(message);
}
window.onerror= function(message,script,line,column,errObj) {
          bridge.reportError(message,script,line,column,errObj);
          return true
}

;""");

        base.getEngine.setJavaScriptEnabled(true)

        base.getEngine.loadContent(htmlString)
        
        TeaIOUtils.writeToFile(new File("debug.html"),htmlString)

        // Listen to view change to be able to reload content
        v.onWith("view.replace") {
          v: AView[_,_] =>
            println(s"!!Replacing VIEW!!")
            targetView = v
            var nhtml = v.rerender
            var htmlString = nhtml.toString()

            onUIThread {
              //var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]
              //window.setMember("base", nhtml)
              //window.setMember("bridge", this);
              base.getEngine.loadContent(htmlString)
              //window.setMember("base", nhtml)
              //window.setMember("bridge", this);
              ////base.getEngine.reload()

              //base.getEngine.executeScript("console.log('Reloaded triggered')")
            }
        }
      }

      // Simple Content Load 
      override def content_=(str: String) = {
        super.content = str
        println(s"Loading: $str")
        this.loadContent(str)
      }

    }

    w.asInstanceOf[VUIWebBrowser[Node, VUIWebBrowser[Node, _]]]

  }

  // Imported Content 
  //----------------------

}


                    
