package com.idyria.osi.vui.core.view

import java.net.URL
import java.net.URLClassLoader

/**
 * @author zm4632
 */
class ExtensibleURLClassLoader(arr:Array[URL],p:ClassLoader) extends URLClassLoader(arr,p) {
  
  def this(arr:Array[URL]) = this(arr,Thread.currentThread().getContextClassLoader)
  def this() = this(Array[URL](),Thread.currentThread().getContextClassLoader)
  def this(p:ClassLoader) = this(Array[URL](),p)
  
  override def addURL(u:URL) = super.addURL(u)
}