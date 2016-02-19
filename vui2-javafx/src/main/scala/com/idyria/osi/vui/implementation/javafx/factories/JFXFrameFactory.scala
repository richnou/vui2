

package com.idyria.osi.vui.implementation.javafx.factories

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import com.idyria.osi.vui.core.definitions.VUIFrame
import com.idyria.osi.vui.core.definitions.VUISGNode
import com.idyria.osi.vui.core.utils.UtilsTrait
import com.idyria.osi.vui.implementation.javafx.JavaFXUtilsTrait
import javafx.event.EventHandler
import javafx.stage.WindowEvent

trait JFXFrameFactory extends com.idyria.osi.vui.core.definitions.VUIFrameFactory[Node] with JavaFXUtilsTrait {

  // Class Fields 
  //---------------------

  // Methods
  //------------------
  override def createFrame: com.idyria.osi.vui.core.definitions.VUIFrame[Node, VUIFrame[Node, _]] = {

    new Stage() with VUIFrame[Stage, VUIFrame[Stage, _]] {

      // Init
      //---------------

      //-- Create a Default Scene with Group
      //var topGroup = new Group
      this.setScene(new Scene(new Group))

      //-- Per default close, don't hide

      // Members declared in com.idyria.osi.vui.core.components.scenegraph.SGGroup
      //-------------------

      /**
       * Override Node Method to add children to Top Group
       */
      this.onMatch("child.added") {

        // Adding A group in the scene replaces the top group
        /*case g: VUISGNode[_,_] =>

          println("Adding Group: " + g.base)
          
          this.getScene().setRoot(g.base.asInstanceOf[javafx.scene.Parent])*/

        // Adding nodes only addes to the top node
        case n: VUISGNode[_, _] =>

          // Create Pane to welcome new node 
          n.base match {
            case node: Pane => this.getScene().setRoot(node)
            case node =>
              println(s"Adding simple node")
              var p = new StackPane
              this.getScene().setRoot(p)
              p.getChildren.add(n.base.asInstanceOf[Node])
          }
        // this.getScene().getRoot().asInstanceOf[Group].getChildren().add()

      }

      /**
       *    Remove scene content by setting an empty group
       */
      override def clear: Unit = {

        this.sceneProperty().get() match {
          case null =>
          case scene => scene.getRoot() match {
            case g: Group => g.getChildren().clear()
            case _ =>
          }
        }

        super.clear

      }

      /**
       * Does nothing
       */
      /* def children: Seq[com.idyria.osi.vui.core.components.scenegraph.SGNode[javafx.stage.Stage]] = {
        Nil
      }

      /**
       * Does nothing
       */
      override def removeChild(c: com.idyria.osi.vui.core.components.scenegraph.SGNode[Stage]): Unit = {

      }*/

      // Members declared in com.idyria.osi.vui.core.components.scenegraph.SGNode
      //-------------------
      def base: javafx.stage.Stage = this

      /**
       * Revalidate requests new layouting
       */
      def revalidate: Unit = this.base.getScene().getRoot().requestLayout()

      /**
       * Name maps to top group id
       */
      override def setName(str: String): Unit = this.base.getScene.getRoot.setId(str)

      // Members declared in com.idyria.osi.vui.core.components.main.VuiFrame
      def height(height: Int): Unit = this.base.setHeight(height)
      def width(width: Int): Unit = this.base.setWidth(width)

      override def size_=(v: Tuple2[Double, Double]) = {
        super.size_=(v)
        this.base.setHeight(v._2)
        this.base.setWidth(v._1)

      }

      override def title_=(title: String): Unit = {

        this.base.setTitle(title)

      }

      override def close() = {
        onUIThread(super.close)
      }

      /*override def show() = {
          onUIThread(super.show())
      }*/

      override def visible_=(v: Boolean) = {
        super.visible = (v)
        v match {
          case true => onUIThread(this.base.show())
          case false =>onUIThread( this.base.close())
        }

      }

      // Events
      //---------------------

      /**
       * When the Window gets closed
       */
      def onClose(cl: => Unit) = {

        this.setOnCloseRequest(new EventHandler[WindowEvent] {
          def handle(e: WindowEvent) = {

            cl
          }
        })

      }
      this.onClose(this.@->("close"))

    }.asInstanceOf[VUIFrame[Node, VUIFrame[Node, _]]]

  }

  // Imported Content 
  //----------------------

}


                    
