package com.idyria.osi.vui.lib.charts

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

/**
 * This trait is to be mixed in anywhere to get access to factory methods that create some default datasets
 */
trait DatasetsBuilder {

  def timeDataset[T: ClassTag](name: String): TimeValuesDataset[T] = {

    var ds = new TimeValuesDataset[T]
    ds.name = name

    ds

  }

  def xyDataset[X: ClassTag, Y: ClassTag](name: String): XYDataset[X, Y] = {
    var ds = new XYDataset[X, Y]
    ds.name = name
    ds
  }
  
  def xyDataset[X: ClassTag, Y: ClassTag](name: String,vals:Iterable[Tuple2[X,Y]]): XYDataset[X, Y] = {
    var ds = new XYDataset[X, Y]
    ds.name = name
    ds.addAll(vals)
    ds
  }
  

  def xyIntDataset(name: String) = xyDataset[Int, Int](name)
  def xyLongDataset(name: String) = xyDataset[Long,Long](name)
  def xyIntLongDataset(name: String) = xyDataset[Int, Long](name)

}

