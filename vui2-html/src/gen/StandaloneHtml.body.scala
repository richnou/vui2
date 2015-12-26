
// Engine Reference
var engine: Option[javafx.scene.web.WebEngine] = None

var actions = Map[String, Any => Any]()

// Register to JS Engine

def registerClosure(cl: Any => Any) = {

  // Id 
  var id = cl.hashCode().toHexString
  actions = actions + (id -> cl)

  id
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