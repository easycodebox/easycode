/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	caches: {
		
	},
	init: function() {
		//缓存表格操作列html
		this.table.cacheOps();
		//初始化模板
		this.vm = new Vue({
			el: '#tmpls',
			data: {
				log: {}
			}
		});
	}
});
$(function(){
	
	//初始化页面
	gb.init();
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/log/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.log = data.data;
			layer.page1(gb.title($btn), $('#loadDialogLog'));
		});
	});
	
});