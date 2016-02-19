package com.idyria.osi.vui.implementation.javafx

import com.idyria.osi.vui.core.utils.UtilsTrait
import javafx.application.Platform
import java.util.concurrent.Semaphore

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
        var res : Any = null
        var err : Throwable = null
        var s = new Semaphore(0)
        Platform.runLater(new Runnable() {
          def run = {
            try {
               res = cl
            } catch {
              case e : Throwable => err = e
            } finally {
              s.release()
            }
           
            

          }
        })
        s.acquire
        
        //-- Return result or propagate error
        (res,err) match {
          case (r,null) => r
          case _ => throw err
        }
    }

  }

}