/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	caches: {
		
	},
	table: {
		/**
		 * 格式化 是否循环
		 */
		fmtIsCycle: function(value, row, index) {
			if (value.className == "YES") {
				return '<div class="handler isCycle switch-close yes" />';
			} else if (value.className == "NO") {
				return '<div class="handler isCycle switch-open no" />';
			}
		}
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
				generator: {}
			}
		});
	},
	initialVal: function(initVal){
		var $form = $(this).closest("form"),
			curVal = $form.find("input[name=currentVal]").val();
		if(!curVal)
			return "请先填写当前值";
		if(initVal.length > curVal.length ||
				(initVal.length == curVal.length && initVal > curVal))
			return "初始值不能大于当前值";
		return true;
	},
	curVal: function(curVal) {
		var $form = $(this).closest("form"),
			initVal = $form.find("input[name=initialVal]").val(),
			oldCurVal = $form.find(".oldCurVal").val(),
			maxVal = $form.find("input[name=maxVal]").val();
		if(!initVal)
			return "请先填写初始值";
		if(curVal.length < initVal.length ||
				(curVal.length == initVal.length && curVal < initVal))
			return "当前值不能小于初始值";
		if(curVal.length < oldCurVal.length ||
				(curVal.length == oldCurVal.length && curVal < oldCurVal))
			return "当前值不能小于" + oldCurVal;
		if(maxVal && (curVal.length > maxVal.length ||
				(curVal.length == maxVal.length && curVal > maxVal)))
			return "当前值不能大于最大值";
		return true;
	},
	maxVal: function(maxVal) {
		var $form = $(this).closest("form"),
			curVal = $form.find("input[name=currentVal]").val();
		if(!curVal)
			return "请先填写当前值";
		if(maxVal.length < curVal.length ||
				(maxVal.length == curVal.length && maxVal < curVal))
			return "最大值不能小于当前值";
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
		targetClass: "isCycle",	//操作成功后修改的目标对象
		idsKey: "generatorTypes",
		ajaxKey: "generatorType",
		url: "/generator/updateIsCycle.json",
		change: {
			"switch-open": {
				confirmMsg	: "启用循环？",
		 		failMsg		: "启用循环失败!",
		 		data: {isCycle: "YES"}
			},
			"switch-close": {
				confirmMsg	: "禁用循环？",
		 		failMsg		: "禁用循环失败!",
		 		data: {isCycle: "NO"}
			}
		}
	});
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/generator/load.json",{generatorType: $btn.data("generatorType")}, function(data) {
			gb.vm.generator = data.data;
			layer.page1(gb.title($btn), $('#loadDialogGenerator'));
		});
	}).on("click", ".updBtn", function() {
		var $btn = $(this);
		$.post("/generator/load.json",{generatorType: $btn.data("generatorType")}, function(data) {
			gb.vm.generator = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'));
		});
	});
	
});