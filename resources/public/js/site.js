$(document).ready(function(){
    $(".collapsable")
  	.each(function() 
  	      { $(this).click( 
  	        function() { 			  
 			  if (this.expanded) {
 			     $(this).children().hide();
 			     this.expanded = false;
 			  } else { 			   
 			     $(this).children().show();
 			     this.expanded = true;
 			  } 			  
	        });});
  		    
  });

