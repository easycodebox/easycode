$(function(){
	$('#rememberMe').iCheck({
    	checkboxClass: 'icheckbox_square-blue'
    });
	
	/* <!-- 显示第三方服务传来的提示信息 --> */
	var scripts = document.getElementsByTagName("script"),
    	data = (new Function('return ' + $.trim(scripts[scripts.length - 1].innerHTML)))();
	if(data && data.msg) {
		$(".errors").text(decodeURIComponent(data.msg));
		var act = $("form").attr("action");
		$("form").attr("action", act.replace(/msg=[^&]*/g, ""));
	}
	
});

