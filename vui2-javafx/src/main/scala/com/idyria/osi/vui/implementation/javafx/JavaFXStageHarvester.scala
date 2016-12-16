package com.idyria.osi.vui.implementation.javafx

import edu.kit.ipe.adl.indesign.core.harvest.Harvester
import edu.kit.ipe.adl.indesign.core.harvest.HarvestedResource
import javafx.stage.Stage
import javafx.beans.value.ObservableValue
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.stage.WindowEvent

object JavaFXStageHarvester extends Harvester {
  
  this.onDeliverFor[FXStageResource] {
    case stage =>
      gather(stage)
      true
  }
  
}

class FXStageResource(val stage:Stage) extends HarvestedResource {
  
  this.root
  
  
  def getId = stage.hashCode.toString()
  
  stage.setOnCloseRequest(new EventHandler[WindowEvent] {
    
    def handle(e:WindowEvent) = {
      println("** CLSOING Stage**")
      FXStageResource.this.rooted = false
      JavaFXStageHarvester.finishGather()
    }
  })
  
  stage.setOnHiding(new EventHandler[WindowEvent] {
    
    def handle(e:WindowEvent) = {
      println("** CLSOING Hiding Stage**")
      FXStageResource.this.rooted = false
      JavaFXStageHarvester.finishGather()
    }
  })
  /*stage.onCloseRequestProperty().addListener(new ChangeListener[java.lang.Boolean] {
    def changed(b: ObservableValue[_ <: java.lang.Number], old: java.lang.Boolean, n: java.lang.Boolean) = {
      
      //println("Resized")
      //scene.
    }
  })
  **/
  
  
}