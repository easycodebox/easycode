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
	$(".handler").changeHandler({
		//操作成功后修改的目标对象
		targetClass: "isCycle",
		idsKey: "generatorType",
		url: "/generator/updateIsCycle.json",
		change: {
			open: {
				confirmMsg	: "启用循环？",
		 		failMsg		: "启用循环失败!",
		 		data: {isCycle: "YES"}
			},
			close: {
				confirmMsg	: "禁用循环？",
		 		failMsg		: "禁用循环失败!",
		 		data: {isCycle: "NO"}
			}
		}
	});
	
	$(".loadBtn").click(function() {
		var $btn = $(this);
		$.post("/generator/load.json",{generatorType: $btn.data("generatorType")}, function(data) {
			gb.vm.generator = data.data;
			layer.page1(gb.title($btn), $('#loadDialogGenerator'));
		});
	});
	
	$(".updBtn").click(function() {
		var $btn = $(this);
		$.post("/generator/load.json",{generatorType: $btn.data("generatorType")}, function(data) {
			gb.vm.generator = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'));
		});
	});
	
});