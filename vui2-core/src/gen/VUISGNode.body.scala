/**
   * base implementatino class, if the node is wrapping
   */
  def base: BT

  var parent: VUISGNode[BT,_] = null

  /**
   * Optional ID for the Node
   */
  var id: String = ""

  /**
   * Optional Name for the node
   */
  var name: String = ""

  /**
   * Set the name
   */
  def setName(str: String) = this.name = str

  /**
   * parent setup
   */
  def setParent(p: VUISGNode[BT,_]) = {

    // Remove from actual if in one
    //---------
    this.parent match {
      case null =>
      case p =>
        p.removeChild(this)
    }

    // Set
    this.parent = p

  }
  
  def getParent[NT <: VUISGNode[BT,_]] : NT= parent.asInstanceOf[NT]

  def getRootParent = {
    var cp = this 
    while(cp.getParent!=null) {
      cp = cp.getParent
    }
    
    cp
  }
  
  def detach = {
    this.setParent(null)
    this
  } 
  
   /**
   * FIXME : Optimise this ?
   */
  def indentCount : Int = {
    if (this.parent == null)
      0
    else {
      1 + this.parent.indentCount
    }
  }
  
  /**
   * This method is a high level call to ask the underlying implementation
   * to make sure the node has been redrawn
   */
  def revalidate

  //def apply[NT <: VUISGNode[BT,_]](cl : (NT => Unit))

  /**
   * When constraints of element are updated, call on layout manager of container to update constraints if needed
   */
  this.on("constraints.updated") {

    this.parent match {
      case null                    =>
      //case p if (p.layout != null) => p.asInstanceOf[VUISGNode[BT,_]].layout.applyConstraints(this, this.fixedConstraints)
      case _                       =>
    }

  }


  var sgChildren = List[VUISGNode[BT,_]]()

  /**
   * This method add a new node to the current container node
   *
   * To Be Overriden by Implementation to actually get a new Node
   *
   */
  def node[NT <: VUISGNode[BT,_]](content: NT): NT = {

    // Change parent
    content.setParent(this.asInstanceOf[VUISGNode[BT,_]])
    content.@->("parent.changed")
    content.@->("parent.changed", this)

    sgChildren = sgChildren :+ content

    // Return and call added
    this.@->("child.added", content)

    // Update layout

    content
  }
  
   /**
   * This method cannot be override to make sure its basic implementation can still be reached
   *
   *
   */
  final def addChild[NT <: VUISGNode[BT,_]](content: NT): NT = {

    // Change parent
    content.setParent(this.asInstanceOf[VUISGNode[BT,_]])
    content.@->("parent.changed")
    content.@->("parent.changed", this)

    sgChildren = sgChildren :+ content

    // Return and call added
    this.@->("child.added", content)

    // Update layout

    content
  }

  /**
   * Alias to node(VUISGNode[BT,_])
   */
  def <=[NT <: VUISGNode[BT,_]](n: NT): NT = this.node(n)

  /**
   * Returns the list of children
   */
  def children: List[VUISGNode[BT,_]] = this.sgChildren

  /**
   * Clear Children components
   */
  def clear: Unit = {
     //super.clear
    var bk = this.sgChildren
    this.sgChildren = this.sgChildren.filter(_ => false)

    bk.foreach {
      c => this.@->("child.removed", c)
    }
    //this.sgChildren = this.sgChildren diff Seq(n)
   

  }

  def removeChild(n: VUISGNode[BT,_]) = {
    this.sgChildren.contains(n) match {
      case true =>
        this.sgChildren = this.sgChildren diff Seq(n)
        this.@->("child.removed", n)
      //println(s"Child Removed");
      case false =>
        //println("Child not removed")
        /*this.sgChildren.foreach {
          n => println(s"node: $n")

        }*/
    }
  }


  // Tree Processing
  //---------------------------

  /**
   * Executes closure on all Subnodes
   */
  def onSubNodes(cl: VUISGNode[BT,_] => Unit): Unit = {
    this.onSubNodesMatch {
      case n => cl(n)
    }
  }

  def onSubNodesMatch(f: PartialFunction[VUISGNode[BT,_], Unit]): Unit = {

    // Stack of nodes to process
    //------------------
    var nodes = scala.collection.mutable.Stack[VUISGNode[BT,_]]()
    this.children.foreach(nodes.push(_))

    while (nodes.isEmpty == false) {

      // Take current
      var current = nodes.pop

      // Execute function
      f(current)

      // Add Children if some
      current match {
        case g: VUISGNode[BT,_] => g.children.foreach(nodes.push(_))
        case _ =>
      }

    }

  }


  // Search 
  //-----------------
  def searchByName(name: String): Option[VUISGNode[BT,_]] = {

    def search(current: VUISGNode[BT,_], name: String): Option[VUISGNode[BT,_]] = {

      //println("Testing for header: " + current.name)

      current match {
        // Found
        case c if (c.name != null && c.name == name) =>

          return Option(current)

        // Not found and is a group -> search
        case g: VUISGNode[BT,_] =>

          var res: Option[VUISGNode[BT,_]] = None
          g.children.find {
            c =>
              search(c, name) match {
                case Some(r) =>
                  res = Option(r); true
                case None => false
              }
          }
          res

        // Not found, and not a group -> 
        case _ => None

      }
    }
    search(this, name)

  }
