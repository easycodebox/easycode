/**
 * 全局属性和方法
 */
utils.extend(window.gb || (window.gb = {}), {
	caches: {
		
	},
	init: function() {
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
	$(".handler").UI_switch({
		//操作成功后修改的目标对象
		targetClass: "status",
		url: "/project/openClose.json"
	});
	
	$(".loadBtn").click(function() {
		var $btn = $(this);
		$.post("/project/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.project = data.data;
			layer.page1(gb.title($btn), $('#loadDialogProject'));
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
	
	$(".updBtn").click(function() {
		var $btn = $(this);
		$.post("/project/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.project = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'));
		});
	});
	
});