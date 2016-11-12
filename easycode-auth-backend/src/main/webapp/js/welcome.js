/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	init: function() {
		//子页面框架初始化
		this.subframe();
		//清除菜单项的状态 - 因为当前页面为欢迎页面，没有对应的菜单按钮
		$(".treeview").removeClass("active");
	}
});
$(function(){
	
	//初始化页面
	gb.init();
	
});