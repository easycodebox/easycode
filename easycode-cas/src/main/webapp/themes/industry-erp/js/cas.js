
$(function(){
	$(".login_form input").focus(function(){
		$(this).closest(".input-box").addClass("input-focus");
	}).blur(function() {
		$(this).closest(".input-box").removeClass("input-focus");
	}).first().focus();
	
	/* <!-- 显示第三方服务传来的提示信息 --> */
	var scripts = document.getElementsByTagName("script"),
    	data = (new Function('return ' + $.trim(scripts[scripts.length - 1].innerHTML)))();
	if(data && data.msg) {
		$(".errors").text(decodeURIComponent(data.msg));
		var act = $("#fm1").attr("action");
		$("#fm1").attr("action", act.replace(/msg=[^&]*/g, ""));
	}
	
});

