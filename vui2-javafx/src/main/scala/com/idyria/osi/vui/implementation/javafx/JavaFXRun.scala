package com.idyria.osi.vui.implementation.javafx

import javafx.application.Platform
import com.sun.javafx.tk.Toolkit
import javafx.application.Application
import javafx.stage.Stage
import java.util.concurrent.Semaphore
import com.idyria.osi.vui.core.VUIFactory
import com.idyria.osi.vui.implementation.javafx.factories.JFXFinalFactory

class JavaFXRun extends Application {

  var cl: () => Unit = { () => }

  def start(stage: Stage) = {
    JavaFXRun.application = this
    stage.close()
    cl()

    // Select VUI implementation for JFX thread
    //---------------
    VUIFactory.setImplementationForCurrentThread(new JFXFinalFactory)

    JavaFXRun.semaphore.release

    //stage.show()
  }

}

object JavaFXRun {

  var application: Application = null
  var semaphore = new Semaphore(0)
  var started = false
  var applicationThread: Option[Thread] = None

  VUIFactory.setImplementationForCurrentThread(new JFXFinalFactory)
  VUIFactory.defaultImplementation = Some(new JFXFinalFactory)

  def onJavaFX[T](cl: => T): Option[T] = {

    started match {
      case true =>

        var r: Option[T] = None
        Platform.runLater(new Runnable() {
          def run = {
            try { r = Some(cl) } finally { semaphore.release }

          }
        })
        //semaphore.acquire()
        r

      // No grants in semaphore, start application
      case false =>

        /*applicationThread = Some(new Thread(new Runnable() {
          def run = {
            
            try {Application.launch(classOf[JavaFXRun])} finally {semaphore.release}
      
          }
        }))
        applicationThread.get.start

        // Wait started
        semaphore.acquire()*/

        // Our Main app does release a credit in the semaphore
        var fxThread = new Thread(new Runnable() {
          def run = {

            try { Application.launch(classOf[JavaFXRun]) } finally {}

          }
        })
        fxThread.start()

        semaphore.acquire()

        started = true;
        var r: Option[T] = None
        Platform.runLater(new Runnable {

          def run = {
            try { r = Some(cl) } finally { semaphore.release }
          }
        })
        // Acquire a semaphore to wait for the end of execution
        semaphore.acquire()
        r

    }

    /* // Check Java FX has been started
    //----------
    //var d = new DummyApplication
    applicationThread match {
      case Some(appThread) =>

       
        
      
        Platform.runLater(new Runnable() {
          def run = {
              try {cl} finally {semaphore.release}
            
          }
        })

      // No grants in semaphore, start application
      case None =>

        /*applicationThread = Some(new Thread(new Runnable() {
          def run = {
            
            try {Application.launch(classOf[JavaFXRun])} finally {semaphore.release}
      
          }
        }))
        applicationThread.get.start

        // Wait started
        semaphore.acquire()*/
        

        Platform.runLater(new Runnable {

          def run = {
             try {cl} finally {semaphore.release}
          }
        })

    }
    
    // Acquire a semaphore to wait for the end of execution
    semaphore.acquire()*/

  }

}

trait VUIJavaFX {
  def onJavaFX[T](cl: => T): T = {
    JavaFXRun.onJavaFX {
      cl
    }.get

  }
}
