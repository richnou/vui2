package com.idyria.osi.vui.core.utils

import com.idyria.osi.vui.core.definitions.VUIBuilder

 
/**
 * Thsi trait contains global utilities
 */
trait UtilsTrait {
  
  // Utils
  //--------------------
  
  /**
   * The provided closure should run on the UI thread
   * The default implementation just runs the closure on current Thread
   * 
   * Should be overriden by implementors
   */
  def onUIThread(cl: => Unit) 
  
}