package com.idyria.osi.vui.implementation.javafx

import com.idyria.osi.vui.core.utils.UtilsTrait
import javafx.application.Platform

trait JavaFXUtilsTrait extends UtilsTrait {
  
  // Utils
  //------------
  override def onUIThread(cl: => Unit) {

    //JavaFXRun.onJavaFX({ cl })

    Platform.runLater(new Runnable() {
      def run = {
        cl

      }
    })

  }
  
}