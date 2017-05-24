

package com.idyria.osi.vui.core

import com.idyria.osi.vui.core.definitions.AbstractVUIFactory
import scala.reflect.ClassTag

/*
 * Factory Object is used to select the underlying Implementation
 */
object VUIFactory {
  
  var defaultImplementation: Option[AbstractVUIFactory[_]] = None

  var selectedImplementations = Map[Thread, AbstractVUIFactory[_]]()

  /**
   *
   */
  def setImplementationForCurrentThread(impl: AbstractVUIFactory[_]) = {

    this.selectedImplementations = selectedImplementations + (Thread.currentThread() -> impl)

  }

  /**
   * Returns the currently selected application by current thread
   */
  def selectedImplementation[T]: AbstractVUIFactory[T] = {

    selectedImplementations.get(Thread.currentThread()) match {
      case Some(impl) => impl.asInstanceOf[AbstractVUIFactory[T]]
      case None => defaultImplementation.asInstanceOf[AbstractVUIFactory[T]]
    }

    //return VUIBuilder.findImplementations.head.asInstanceOf[VUIBuilder[T]]

    //return SwingVUIImpl.asInstanceOf[VUIBuilder[T]]

    //return actualImplementation.asInstanceOf[VUIBuilder[T]]

  }

  def findImplementations: Set[AbstractVUIFactory[_]] = {

    return Set()

  }

  // Implementation getters
  //---------------
  def as[T](implicit tag: ClassTag[T]): T = {

    tag.runtimeClass.isAssignableFrom(this.selectedImplementation.getClass()) match {
      case true => this.selectedImplementation.asInstanceOf[T]
      case false => throw new RuntimeException("Currently selected implementation does not support interface " + tag)
    }

  }
}