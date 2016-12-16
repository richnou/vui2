package com.idyria.osi.vui.implementation.javafx

import com.idyria.osi.vui.core.utils.UtilsTrait
import javafx.application.Platform
import java.util.concurrent.Semaphore
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.beans.value.ObservableValue
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.beans.property.ReadOnlyObjectProperty
import java.net.URL
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.image.Image
import javafx.scene.Node
import javafx.beans.property.ObjectProperty
import javafx.scene.control.Control
import javafx.scene.layout.Pane
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.event.Event

trait JavaFXUtilsTrait extends UtilsTrait {

  // Utils
  //------------
  override def onUIThread(cl: => Any): Unit = {

    //JavaFXRun.onJavaFX({ cl })
    Platform.isFxApplicationThread() match {
      case true =>
        cl
      case false =>
        Platform.runLater(new Runnable() {
          def run = {
            cl

          }
        })
    }

  }

  override def onUIThreadBlocking(cl: => Any): Any = {

    //JavaFXRun.onJavaFX({ cl })
    Platform.isFxApplicationThread() match {
      case true =>
        cl
      case false =>
        var res: Any = null
        var err: Throwable = null
        var s = new Semaphore(0)
        Platform.runLater(new Runnable() {
          def run = {
            try {
              res = cl
            } catch {
              case e: Throwable => err = e
            } finally {
              s.release()
            }

          }
        })
        s.acquire

        //-- Return result or propagate error
        (res, err) match {
          case (r, null) => r
          case _ => throw err
        }
    }

  }

  // Listeners
  //-----------------

  def onEvent[ET <: Event](cl: ET => Unit): EventHandler[ET] = {

    new EventHandler[ET] {
      def handle(event: ET) = {
        cl(event)
      }
    }

  }

  def onSelected(node: CheckBox)(cl: (Boolean => Unit)) = {
    node.selectedProperty().addListener(new ChangeListener[java.lang.Boolean] {
      def changed(b: ObservableValue[_ <: java.lang.Boolean], old: java.lang.Boolean, n: java.lang.Boolean) = {
        cl(n)

      }
    })
  }

  def onClick(node: Button)(cl: => Unit) = {

    node.setOnMouseClicked(new EventHandler[MouseEvent] {
      def handle(event: MouseEvent) = {
        cl
      }
    })

  }

  def onMouseEvent(h: ObjectProperty[EventHandler[MouseEvent]])(cl: (MouseEvent => Unit)) = {

    h.setValue(new EventHandler[MouseEvent] {
      def handle(event: MouseEvent) = {
        cl(event)
      }
    })

  }

  def onMouseClicked(n: Control)(cl: (MouseEvent => Unit)) = {
    n.setOnMouseClicked(new EventHandler[MouseEvent] {
      def handle(event: MouseEvent) = {
        cl(event)
      }
    })

  }

  def onMouseClicked(n: Pane)(cl: (MouseEvent => Unit)) = {
    n.setOnMouseClicked(new EventHandler[MouseEvent] {
      def handle(event: MouseEvent) = {
        cl(event)
      }
    })

  }

  def onIntPropertyChange(prop: ReadOnlyObjectProperty[Integer])(cl: Int => Unit) = {
    prop.addListener(new ChangeListener[java.lang.Integer] {
      def changed(b: ObservableValue[_ <: java.lang.Integer], old: java.lang.Integer, n: java.lang.Integer) = {
        cl(n)

      }
    })

  }

  def onLongPropertyChange(prop: ReadOnlyObjectProperty[Long])(cl: Long => Unit) = {
    prop.addListener(new ChangeListener[Long] {
      def changed(b: ObservableValue[_ <: Long], old: Long, n: Long) = {
        cl(n)

      }
    })

  }

  def onBooleanPropertyChange(prop: ReadOnlyBooleanProperty)(cl: Boolean => Unit) = {
    prop.addListener(new ChangeListener[java.lang.Boolean] {
      def changed(b: ObservableValue[_ <: java.lang.Boolean], old: java.lang.Boolean, n: java.lang.Boolean) = {
        cl(n)

      }
    })

  }

  def onDoublePropertyChange(prop: ReadOnlyObjectProperty[Double])(cl: Double => Unit) = {
    prop.addListener(new ChangeListener[Double] {
      def changed(b: ObservableValue[_ <: Double], old: Double, n: Double) = {
        cl(n)

      }
    })

  }

  def imageLabel(u: URL, text: String = ""): Label = {
    new Label(text, new ImageView(new Image(u.toExternalForm())))
  }

  def imageLabel(resource: String): Label = {
    imageLabel(getClass.getClassLoader.getResource(resource), "")
  }

}