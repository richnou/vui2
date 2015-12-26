/**
   * Bind the execution of the Closure to a call through embedded JS
   */
  override def onClick(cl: => Any) = {

    var clid = this.getRootParent.asInstanceOf[StandaloneHtml[BT,_]].registerClosure {
      _ => cl
      //
    }
    this("onclick" -> s"base.call('$clid',this)")

  }