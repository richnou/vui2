package com.idyria.osi.vui.implementation.javafx


import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.Group
import scala.collection.JavaConversions
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.input.KeyEvent
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import scala.language.implicitConversions
import java.awt.Robot
import com.idyria.osi.vui.core.definitions.VUIComponent
import com.idyria.osi.vui.core.definitions.VUIClickEvent
import com.idyria.osi.vui.core.definitions.VUISGNode
import com.idyria.osi.vui.core.definitions.VUIMouseEvent
import com.idyria.osi.vui.core.definitions.VUIDragEvent

class JavaFXNodeDelegate[DT <: Node,+Self](var delegate: DT) extends VUIComponent[DT,Self] {
  
  this : Self => 
    
  //---------------------------------------
  // Node
  //---------------------------------------

  /**
   * Name maps to ID
   */
  override def setName(str: String) = {
    //super.setName(str)
    delegate.setId(str)
  }

  def base: DT = delegate
  override def revalidate = delegate match {
    case g: Group => g.requestLayout()
    case _ =>
  }

  override def clear: Unit = {

    delegate match {
      case g: Group => g.getChildren().clear();
      case p: Pane => p.getChildren().clear()
      case _ =>

    }

  }

  /**
   *
   */
  override def removeChild(c: VUISGNode[DT,_]) = delegate match {
    case g: Group => g.getChildren().remove(c.base)
    case _ =>
  }

  /**
   * Do not return anything
   */
  /* def children: Seq[SGNode[Node]] = delegate match {
    case g: Group => g.getChildren().toArray().map(c => new JavaFXNodeDelegate[Node](c.asInstanceOf[Node]))
    case _        => Nil
  }*/

  //---------------------------------------
  // General
  //---------------------------------------

  //-- Enable / Disable

   def disable = delegate.setDisable(true)
   def enable = delegate.setDisable(false)

  //-- Visibility
  override def visible_=(v: Boolean) =  {
    delegate.setVisible(v)

  }

  //---------------------------------------
  // Actions
  //---------------------------------------

   def onMousePressed(action: VUIMouseEvent => Unit) = {

    /*delegate.addMouseListener(new MouseAdapter() {

      override def mousePressed(e: MouseEvent) = {
        action(populateVUIMouseEvent(e, new VUIMouseEvent))
      }

    })*/

  }

   def onDrag(action: VUIMouseEvent => Unit) = {

    /* delegate.addMouseMotionListener(new MouseMotionAdapter() {

      override def mouseDragged(ev: MouseEvent) = {
        action(populateVUIMouseEvent(ev, new VUIMouseEvent))
      }

      /*override def mouseDrag(evt, x, y) = {

          }*/

    })*/

  }

   def onClicked(action: VUIClickEvent => Any) = {

    delegate.setOnMouseClicked(new EventHandler[MouseEvent] {
      def handle(event: MouseEvent) = {
        action(event)
      }
    })

  } /*delegate.addMouseListener(new MouseAdapter() {
   
    /*override def mouseClicked(e: MouseEvent) = SwingUtilities.invokeLater(new Runnable {

      override def run() = action(e)
    })*/
  
  })*/

  //----------------------
  //-- Geometry listeners
   def onShown(action: => Unit) = {

    var wrapper: (() => Unit) = {
      () => action
    }

    delegate.parentProperty().addListener(new ChangeListener[Parent] {
      def changed(b: ObservableValue[_ <: Parent], old: Parent, n: Parent) = {
        action
      }
    })

    /* delegate.addComponentListener(new ComponentAdapter() {

      override def componentShown(e: ComponentEvent) = wrapper()
      override def componentResized(e: ComponentEvent) = wrapper()
    })

    delegate.addAncestorListener(new AncestorListener() {

      var wrapper: (() => Unit) = {
        () => action
      }

      override def ancestorAdded(e: AncestorEvent) = wrapper()
      override def ancestorMoved(e: AncestorEvent) = {}
      override def ancestorRemoved(e: AncestorEvent) = {}

    })*/

  }

  //------------------------
  // Keyboard
  //------------------------
   def onKeyPressed(cl: Char => Unit) = {
    this.delegate.setOnKeyPressed(new EventHandler[KeyEvent] {
      def handle(e: KeyEvent) = {
        cl(e.getCharacter().charAt(0))
      }
    })
  }

   def onKeyTyped(cl: Char => Unit) = {
    this.delegate.setOnKeyTyped(new EventHandler[KeyEvent] {
      def handle(e: KeyEvent) = {
        cl(e.getCharacter().charAt(0))
      }
    })
  }

   def pressEnter = {
    /*// Prepar Event 
     var ke = new KeyEvent(KeyEvent.ANY,"","",javafx.scene.input.KeyCode.ENTER,false,false,false,false)
     
     // Fire
     this.delegate.fireEvent(ke)*/
    var r = new Robot();
    r.keyPress(java.awt.event.KeyEvent.VK_ENTER)
    r.keyRelease(java.awt.event.KeyEvent.VK_ENTER)
  }

  //---------------------------------------
  // Positioning
  //---------------------------------------
  def setPosition(x: Int, y: Int) = { delegate.setLayoutX(x); delegate.setLayoutY(y) }
  def getPosition: Pair[Int, Int] = Pair[Int, Int](delegate.getLayoutX().toInt, delegate.getLayoutY().toInt)

  //----------------------
  // Styling
  //----------------------

  def size(width: Int, height: Int) = {
    delegate.prefWidth(width)
    delegate.prefHeight(height)
  }

  // Conversions
  //-----------------------

  //-- Convert component events
  //-----------------------
  def convertMouseEventToVUIDragEvent(ev: MouseEvent): VUIDragEvent = {
    this.populateVUIMouseEvent[VUIDragEvent](ev, new VUIDragEvent)
  }

  //-- Mouse Events
  //-----------------------
  implicit def convertMouseEventToVUIMouseEvent(ev: MouseEvent): VUIMouseEvent = {
    this.populateVUIMouseEvent[VUIMouseEvent](ev, new VUIMouseEvent)
  }

  implicit def convertMouseEventToClickEvent(ev: MouseEvent): VUIClickEvent = {

    // Common
    var click = this.populateVUIMouseEvent[VUIClickEvent](ev, new VUIClickEvent)

    // Click
    click.clickCount = ev.getClickCount()
    click
  }
 
  private def populateVUIMouseEvent[ET <: VUIMouseEvent](srcEvent: MouseEvent, targetEvent: ET): ET = {

    // Fill in positions
    //-----------
    targetEvent.actualX = srcEvent.getX().toInt
    targetEvent.actualY = srcEvent.getY().toInt

    // Click counts and button
    //-------------------

    targetEvent
  }

  //override def toString = delegate.toString

}

object JavaFXNodeDelegate {

  implicit def convertJFXNodeToSGNode(node: Node) = {
    new JavaFXNodeDelegate[Node,VUISGNode[Node,_]](node)
  }

  def apply(node: Node) = {
    new JavaFXNodeDelegate[Node,VUISGNode[Node,_]](node)
  }
}