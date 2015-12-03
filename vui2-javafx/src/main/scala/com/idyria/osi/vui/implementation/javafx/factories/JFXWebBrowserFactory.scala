

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
      /*this.base.getEngine.documentProperty().addListener(new javafx.beans.value.ChangeListener[org.w3c.dom.Document] {
        def changed(ov: javafx.beans.value.ObservableValue[_ <: org.w3c.dom.Document], oldState: org.w3c.dom.Document, newState: org.w3c.dom.Document) = {

          onUIThread {
                var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]
              window.setMember("bridge", lc);
              window.setMember("base", targetView.renderedNode.get)
              window.setMember("base", targetView.renderedNode.get)
              window.setMember("base", targetView.renderedNode.get)
              window.setMember("base", targetView.renderedNode.get)
              targetView.renderedNode.get.asInstanceOf[JSEngineReference].engine = Some(base.getEngine)
              base.getEngine.executeScript("""
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
              window.eval("vuiInit.resolve();")
              }

        }
      })*/

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
                window.setMember("bridge", lc);

                if (targetView != null && targetView.renderedNode != None) {
                  window.setMember("base", targetView.renderedNode.get)
                  //document.setMember("base", targetView.renderedNode.get)
                  targetView.renderedNode.get match {
                    case html : StandaloneHtml[_,_] => 
                    case _ => 
                  }
                 // targetView.renderedNode.get.asInstanceOf[JSEngineReference].engine = Some(base.getEngine)
                }

                base.getEngine.executeScript("""
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
              //  window.eval("vuiInit.notify();")
              }
              // var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]

            }
          }
        });

      // I/O redirection
      //-------------------

      // Loading/Reloading
      //------------------------
      def loadContent(str: String) = {
        base.getEngine.loadContent(str)
      }

      def loadURL(str: String) = {
        base.getEngine.load(str)
      }

      override def view_=(v: AView[_,_]) = {
        targetView = v

        // Add Content
        var html = targetView.render
        println(s"Rendered HTML node (" + hashCode() + "): " + html.hashCode() + "//" + targetView.renderedNode.get.hashCode())
        var htmlString = html.toString()
        println(s"Rendered HTML node (" + hashCode() + "): " + html.hashCode() + "//" + targetView.renderedNode.get.hashCode())
        println(s"HTML: $htmlString")
        /* var window = base.getEngine.executeScript("window").asInstanceOf[netscape.javascript.JSObject]
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

;""");*/

        base.getEngine.setJavaScriptEnabled(true)

        base.getEngine.loadContent(htmlString)

        // Listen to view change to be able to reload content
        v.onWith("view.replace") {
          v: AView[_,_] =>
            println(s"!!Replacing VIEW!!")
            targetView = v
            var nhtml = v.render
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


                    
