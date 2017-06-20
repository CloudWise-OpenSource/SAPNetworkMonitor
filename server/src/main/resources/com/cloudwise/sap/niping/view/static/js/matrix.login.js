$(document).ready(function() {

	var login = $('#loginform');
	var recover = $('#recoverform');
	var speed = 400;

	$('#to-recover').click(function() {

		$("#loginform").slideUp();
		$("#recoverform").fadeIn();
	});
	$('#to-login').click(function() {

		$("#recoverform").hide();
		$("#loginform").fadeIn();
	});

	$('#to-login').click(function() {

	});
	
	if(/msie/.test(navigator.userAgent.toLowerCase()) && $.browser.version.slice(0, 3) < 10) {
		$('input[placeholder]').each(function() {

			var input = $(this);

			$(input).val(input.attr('placeholder'));

			$(input).focus(function() {
				if(input.val() == input.attr('placeholder')) {
					input.val('');
				}
			});

			$(input).blur(function() {
				if(input.val() == '' || input.val() == input.attr('placeholder')) {
					input.val(input.attr('placeholder'));
				}
			});
		});

	}
	      
    //平台、设备和操作系统  
    
    
    function checkPC(){
    		var system ={  
	        win : false,  
	        mac : false,  
	        xll : false  
	    };  
	    
	    //检测平台  
	    var p = navigator.platform;        
	      
	    system.win = p.indexOf("Win") == 0;  
	    system.mac = p.indexOf("Mac") == 0;  
	    system.x11 = (p == "X11") || (p.indexOf("Linux") == 0);  
	    //跳转语句  
	    if(system.win||system.mac||system.xll){//转向后台登陆页面  
	        return true; 
	    }else{  
	        return false;
	    }  
    }
    
    if(!checkPC()){
    		$("#loginbox .main_input_box input").css("margin-bottom","0px");
    }else{
    		console.log(navigator.platform)
    }
     
});