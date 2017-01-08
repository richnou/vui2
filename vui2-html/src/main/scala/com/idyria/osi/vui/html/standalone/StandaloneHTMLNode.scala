

package com.idyria.osi.vui.html.standalone


trait StandaloneHTMLNode[BT <: org.w3c.dom.html.HTMLElement, +Self] extends com.idyria.osi.vui.html.HTMLNode[BT, Self] {

  this: Self =>

  // Class Fields 
  //---------------------

  // Init Section 
  //----------------------

  // Methods
  //------------------

  // Imported Content 
  //----------------------
  // Imported from E:/Common/Projects/git/vui2/vui2-html/src/gen/StandaloneHTMLNode.body.scala

  import org.w3c.dom.html.HTMLElement

  /**
   * Bind the execution of the Closure to a call through embedded JS
   */
  override def onClick(cl: => Any) = {

    
    //this("onclick" -> s"base.call('$clid',this)")

  }

  override def doClick = {

    this.attributes.get("onclick") match {
      case Some(value) =>

       

      case None =>
    }
  }

  /**
   *
   */
  override def updateContent = {

   
    

    //var contentNode

  }

}


                    
