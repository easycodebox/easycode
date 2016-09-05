
$(function(){
	
	/* <!-- 显示第三方服务传来的提示信息 --> */
	var scripts = document.getElementsByTagName("script"),
    	data = (new Function('return ' + $.trim(scripts[scripts.length - 1].innerHTML)))();
	if(data && data.msg) {
		var act = $("#fm1").attr("action");
		$("#fm1").attr("action", act.replace(/msg=[^&]*/g, ""));
		alert(decodeURIComponent(data.msg));
	}
	
	var err = $.trim($(".errors").text());
	if(err) {
		alert(err);
	}
	
});

