

package com.idyria.osi.vui.html.standalone

 class StandaloneHtml[BT <: org.w3c.dom.html.HTMLElement,+Self] extends com.idyria.osi.vui.html.Html[BT,Self] with StandaloneHTMLNode[BT,Self]  {

    this:Self => 


    // Class Fields 
    //---------------------
    

    // Init Section 
    //----------------------
    

    // Methods
    //------------------
    

    // Imported Content 
    //----------------------
    // Imported from E:/Common/Projects/git/vui2/vui2-html/src/gen/StandaloneHtml.body.scala

// Engine Reference
var engine: Option[com.idyria.osi.vui.html.js.JSEngine] = None

var actions = Map[String, Any => Any]()

// Register to JS Engine

def registerClosure(cl: Any => Any) = {

  // Id 
  var id = cl.hashCode().toHexString
  actions = actions + (id -> cl)

  id
}
def getClosure(code:String) : Option[Any => Any] = {
  
  this.actions.get(code)
}

def call(code: String, param: AnyRef) = {

  println(s"Calling for action $code, contained:  ${actions.contains(code)}")
  //println(s"Calling with ${param.toString()}")
  actions.get(code) match {
    case Some(action) => action(param)
    case None => throw new RuntimeException("Cannot find action to call");
  }

}


def drop(e: netscape.javascript.JSObject) = {
  println(s"Calling Drop: " + e.getMember("dataTransfer").asInstanceOf[netscape.javascript.JSObject].call("getData", "text"))
}

}


                    
