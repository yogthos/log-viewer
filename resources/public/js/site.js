$(document).ready(function(){

    $(".collapsable-ex")
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
	        
	 $(".collapsable-message")
  	 .each(function() 
  	      { $(this).click( 
  	        function() { 			  
 			  if (this.collapsed) {
 			  	 $(this).find('#info').remove(); 			     			     
 			     $(this).children().show();
 			     this.collapsed = false; 			     
 			  } else { 			 
 			  	 $(this).children().hide();
 			     $(this).append("<div id='info'>click to view</div>"); 			     
 			     this.collapsed = true;
 			  } 			  
	        });});       
  });

