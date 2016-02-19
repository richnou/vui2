console.info("In VUI Standalone")

var vuiInit = jQuery.Deferred();

function vuiStart () {
	
	vuiInit.notify();
	
}

function vuiUpdatedContent() {
	vuiBind();
}
function vuiBind() {
	
  $("[vui-bind]").each(function(index,elt) {
      console.info("Binding "+elt+" to "+$(elt).attr('vui-bind'));
      
      base.call($(elt).attr('vui-bind'),elt);

      $(elt).removeAttr('vui-bind');
  });
	
}

// Init Sequence
//--------------
vuiInit.progress(function() {

  
  console.info("Trying to bind ");
  vuiBind();
  

});