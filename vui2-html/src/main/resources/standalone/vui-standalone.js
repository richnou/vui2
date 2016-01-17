var vuiInit = jQuery.Deferred();

function vuiStart () {
	
	vuiInit.notify();
	
}
vuiInit.progress(function() {

  
  console.info("Trying to bind ");
  $("[vui-bind]").each(function(index,elt) {
      console.info("Binding "+elt+" to "+$(elt).attr('vui-bind'));
      
      base.call($(elt).attr('vui-bind'),elt);

      $(elt).removeAttr('vui-bind');
  });

});