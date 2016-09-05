
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
	
	var tab = $('.tabbox').each(function(index, element) {
        var tl = $(this).find('.tab_top'),
			bt = $(this).find('.tab_bottom');
			bt.find('.tab_item').eq(0).addClass('on');
			tl.find('li').click(function(){
					$(this).addClass('cur').siblings().removeClass('cur');
					var i = $(this).attr('data-item');
					bt.find('div').removeClass('on');
					bt.find("div[data-item="+ i +"]").addClass('on');
				});
    });
		
});

