$(function(){
	$('#rememberMe').iCheck({
    	checkboxClass: 'icheckbox_square-blue'
    });
	
	/* <!-- 显示第三方服务传来的提示信息 --> */
	var scripts = document.getElementsByTagName("script"),
    	data = (new Function('return ' + $.trim(scripts[scripts.length - 1].innerHTML)))();
	if(data && data.msg) {
		var $form = $("form"), $msg = $("#msg");
		if ($msg.length == 0) {
			$form.prepend('<p id="msg" class="login-box-msg errors"></p>');
			$msg = $("#msg");
		}
		$msg.text(decodeURIComponent(data.msg));
		$form.attr("action", $form.attr("action").replace(/msg=[^&]*/g, ""));
	}
	
});

