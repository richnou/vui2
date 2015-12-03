package com.idyria.osi.vui.core.builders

import scala.language.dynamics
import scala.collection.immutable.Stack
import com.idyria.osi.vui.core.definitions.VUISGNode

trait RebuildableNode[T,+Self] extends VUISGNode[T,Self] {
  this:Self => 
    
  var _contentClosure: Option[(() => Any)] = None

}

trait TreeBuilder[BT,MNT <: VUISGNode[BT,_]] extends Dynamic {

  var nodesStack = scala.collection.mutable.Stack[VUISGNode[BT,_]]()
  var topNodes = List[MNT]()

  var currentNode: MNT = _

  //def createNode(name: String): VUISGNode[BT,_]

  /**
   * Add an already build node to current
   */
  def add[NT <: MNT](node: NT): NT = switchToNode(node, {})

  def onNode[NT <: MNT](node: NT)(cl: => Any) = switchToNode(node, cl)

  // Rebuild current node or target node
  //-----------------
  def rebuild = currentNode match {
    case rn: RebuildableNode[_,_] if (rn._contentClosure != None) =>
      rn.clear
      onNode(rn.asInstanceOf[MNT]) {
        rn._contentClosure.get()
      }
      
      rn.@->("rebuild")
    case _ =>
  }

  def switchToNode[NT <: MNT](node: NT, cl: => Any): NT = {

    // Try to create node based on name
    //-------
    node match {
      case rn: RebuildableNode[_,_] if (rn._contentClosure == None) => rn._contentClosure = Some({ () => cl })
      case _ =>
    }

    currentNode = node

    //var node = createNode(name)
    node match {

      // If group, add  - stack - execute - destack
      case n: VUISGNode[BT,_] =>

        //println(s"Adding Group NODE "+nodesStack.headOption)

        // Add TO top of stack if necessary
        //---------------
        if (n.parent == null) {
          nodesStack.headOption match {
            case Some(head) => head <= n
            case _ =>
            // println(s"--> New top node on ${this.hashCode}")
            //topNodes = topNodes :+ n.asInstanceOf[BT]
          }
        }

        //nodesStack = nodesStack.push(n)
        nodesStack.push(n)
        cl

        nodesStack.pop()
        //nodesStack = nodesStack.pop

        // Switch back to top of stack
        // If no nodes, save in top nodes
        nodesStack.headOption match {
          case Some(head) => currentNode = head.asInstanceOf[MNT]
          case _ => topNodes = topNodes :+ n.asInstanceOf[MNT]
        }

      // If node, add - execute
      // If no nodes, save in top nodes
      case n: VUISGNode[BT,_] =>

        //println(s"Adding Simple NODE "+nodesStack.headOption)
        if (n.parent == null) {
          nodesStack.headOption match {
            case Some(head) => head <= n
            case _ => topNodes = topNodes :+ n.asInstanceOf[MNT]
          }
        }

        cl

    }

    // Return
    return node

  }

  /*
  def applyDynamic(name:String)(cl: => Any) : VUISGNode[Any,_] = {
    
    // Try to create node based on name
    //-------
    var node = createNode(name)
    node match {
      
      case null => throw new RuntimeException(s"Tree builder cannot build node: $name, no implementation provided ")
      
       // If group, add  - stack - execute - destack
      case n : VUISGNode[Any,_] => 
        
        nodesStack.headOption match {
          case Some(head) => head <= n
          case _ =>
        }
        
        nodesStack = nodesStack.push(n)
        
        cl
        
        nodesStack = nodesStack.pop
        
      
      // If node, add - execute
      case n : VUISGNode[_] => 
        
        nodesStack.headOption match {
          case Some(head) => head <= n
          case _ =>
        }
        
        cl
        
    }
    
    // Return
    return node
    
  }*/

}