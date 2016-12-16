package com.idyria.osi.vui.implementation.javafx

import javafx.scene.Node
import edu.kit.ipe.adl.indesign.core.harvest.HarvestedResource

class JFXNodeResource(val node:Node) extends HarvestedResource {
  def getId = node.getId
}