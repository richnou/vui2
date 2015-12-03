package com.idyria.osi.vui.core.utils
 
/*
/**
 * This trait defines the apply functions needed to customize a component at creation time
 */
trait ApplyTrait {
  
  //type Self <: ApplyTrait
  
  def apply(cl: Self => Unit) : Self = {
    cl(this.asInstanceOf[Self])
    this.asInstanceOf[Self]
  }
}

trait ApplyParamTrait[T] {
  
  def apply(cl: T => Unit) : T = {
    cl(this.asInstanceOf[T])
    this.asInstanceOf[T]
  }
  
}*/

trait ApplyTrait[+Self] {
  
  this : Self => 
  //type Self <: ApplyTrait
  
  def apply(cl: Self => Unit) : Self = {
    cl(this)
    this
  }
}