/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	caches: {
		
	},
	init: function() {
		//子页面框架初始化
		this.subframe();
		//初始化表格
		$("#data-table").bootstrapTable();
		//缓存表格操作列html
		this.table.cacheOps();
		//初始化模板
		this.vm = new Vue({
			el: '#tmpls',
			data: {
				project: {}
			}
		});
	},
	existName: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		$.ajax({
			async: false,
			type: "POST",
			url: "/project/existName.json",
			data: {
				name: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "项目名已被占用";
		else
			return true;
	},
	existProjectNo: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		$.ajax({
			async: false,
			type: "POST",
			url: "/project/existProjectNo.json",
			data: {
				projectNo: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "项目编码已被占用";
		else
			return true;
	}
});
$(function(){
	
	//初始化页面
	gb.init();
	
	//启用、禁用 功能
	$("#toolbar, #data-table").UI_switch({
		srcSelector: ".handler",//触发事件的对象
		scopeSelector: "#data-table",//操作的dom范围
		targetClass: "status",	//操作成功后修改的目标对象
		url: "/project/openClose.json"
	});
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/project/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.project = data.data;
			layer.page1(gb.title($btn), $('#loadDialogProject'));
		});
	}).on("click", ".updBtn", function() {
		var $btn = $(this);
		$.post("/project/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.project = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'));
		});
	});
	
	$("#addBtn").click(function() {
		var $btn = $(this);
		//初始化选项默认值
		gb.vm.project = {
			status: {
				className: "OPEN"
			},
			sort: 0
		};
		gb.show(gb.title($btn), $('#addDialog'));
	});
	
});