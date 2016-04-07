package com.idyria.osi.vui.core.view

import org.scalatest.FunSuite
import com.idyria.osi.vui.core.definitions.VUISGNode
import java.io.File

class DummyBack {
  
}
class ViewClass extends AView[DummyBack,VUISGNode[DummyBack,_]] with VUISGNode[DummyBack,ViewClass] {
   def base: com.idyria.osi.vui.core.view.DummyBack =  {
     null
   }
   def revalidate: Unit =  {
     
   }
}