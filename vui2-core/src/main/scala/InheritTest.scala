

// Main
//--------------
trait TreeBuilder[NT] {

  def addNode[CN <: NT](nd: CN): CN = {

    nd
  }

}

trait HTMLTreeBuilder extends TreeBuilder[HTMLNode] {

  def span = addNode(new Span)

  def div = addNode(new Div)

}

trait HTMLNode { 

  def :#:(id: String) = {
    
   // implicitly[TN](this.asInstanceOf[TN])
    //this.asInstanceOf[TN]
    this
  }

}

class Span extends HTMLNode {

}
class Div extends HTMLNode {

}

// Sub
//----------------
trait MyHTMLNode extends HTMLNode {

}

trait MyHTMLTReeBuilder extends HTMLTreeBuilder {

  override def div = addNode(new Div with MyHTMLNode)

}

// Extra Interface
//-------------------------
trait MyComponentBuilder extends HTMLTreeBuilder {

  def myComp = {
    div
  }

}

object InheritTest extends App with MyHTMLTReeBuilder with MyComponentBuilder {

  var d = div

  var comp = myComp
  println(s"Comp is really: " + comp.getClass)

  var iddiv = "test" :#: div

}