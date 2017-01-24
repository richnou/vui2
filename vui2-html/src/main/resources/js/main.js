$(document).ready(function(){
	
	$.site('enable debug'); 
	
	$('.ui.shape').shape();
	$('.ui.dropdown').dropdown();
	//$('.ui.menu').menu();
	$('.ui.menu .item').tab();
	
	$('.shape').shape('flip up');
	
});

function allowDrop(ev) {
    ev.preventDefault();
}