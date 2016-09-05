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
				partner: {}
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
			url: "/partner/existName.json",
			data: {
				name: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "合作商名已被占用";
		else
			return true;
	}
});
$(function(){
	
	//初始化页面
	gb.init();
	
	//启用、禁用 功能
	$(".handler").changeHandler({
		//操作成功后修改的目标对象
		targetClass: "status",
		url: "/partner/openClose.json"
	});
	
	//新增功能 - 上传图片
	$("#uploadImgsAdd").uploadImg({
		id: "addImg",
		picType: "partnerContract",
		picInput: "contractAdd",
		imgRule: "r150c150",
		picDiv: "uploadImgsAdd",
		uploadTitle: "upImgAdd"
	});
	
	//修改功能 - 上传图片
	$("#uploadImgsUpd").uploadImg({
		id: "updImg",
		picType: "partnerContract",
		picInput: "contractUpd",
		imgRule: "r150c150",
		picDiv: "uploadImgsUpd",
		uploadTitle: "upImgUpd"
	});
	
	$(".loadBtn").click(function() {
		var $btn = $(this);
		$.post("/partner/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.partner = data.data;
			layer.page1(gb.title($btn), $('#loadDialogPartner'));
		});
	});
	
	$("#addBtn").click(function() {
		var $btn = $(this);
		//初始化选项默认值
		gb.vm.partner = {
			status: {
				className: "OPEN"
			},
			sort: 0
		};
		//重置图片
		$.uploadImg.clear("addImg");
		
		gb.show(gb.title($btn), $('#addDialog'));
	});
	
	$(".updBtn").click(function() {
		var $btn = $(this);
		$.post("/partner/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.partner = data.data;
			//初始化图片
			if(data.data.contract) {
				$.uploadImg.setImg("updImg", data.data.contract);
			}
			gb.show(gb.title($btn), $('#updDialog'));
		});
	});
	
});