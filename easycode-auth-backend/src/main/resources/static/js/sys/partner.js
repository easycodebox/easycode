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
				partner: {}
			}
		});
	},
	existName: function(val) {
		var exist,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		$.ajax({
			async: false,
            tips: false,
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
        if (exist === undefined)
            return "服务异常";
        else if (exist)
			return "合作商名已被占用";
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
		url: "/partner/openClose.json"
	});
	
	//新增功能 - 上传图片
	$("#uploadImgsAdd").uploadImg({
		id: "addImg",
		fileKey: "partnerContract",
		picInput: "contractAdd",
		imgRule: "r150c150",
		picDiv: "uploadImgsAdd",
		uploadTitle: "upImgAdd"
	});
	
	//修改功能 - 上传图片
	$("#uploadImgsUpd").uploadImg({
		id: "updImg",
		fileKey: "partnerContract",
		picInput: "contractUpd",
		imgRule: "r150c150",
		picDiv: "uploadImgsUpd",
		uploadTitle: "upImgUpd"
	});
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/partner/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.partner = data.data;
			layer.page1(gb.title($btn), $('#loadDialogPartner'));
		});
	}).on("click", ".updBtn", function() {
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
	
});