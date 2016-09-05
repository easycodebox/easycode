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
				role: {}
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
			url: "/role/existName.json",
			data: {
				name: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "角色名已被占用";
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
		url: "/role/openClose.json"
	});
	
	$(".loadBtn").click(function() {
		var $btn = $(this);
		$.post("/role/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.role = data.data;
			layer.page1(gb.title($btn), $('#loadDialogRole'));
		});
	});
	
	$("#addBtn").click(function() {
		var $btn = $(this);
		//初始化选项默认值
		gb.vm.role = {
			status: {
				className: "OPEN"
			},
			sort: 0
		};
		gb.show(gb.title($btn), $('#addDialog'));
	});
	
	$(".updBtn").click(function() {
		var $btn = $(this);
		$.post("/role/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.role = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'));
		});
	});
	
	$(".cfgOpsBtn").click(function() {
		var $btn = $(this),
			id = $btn.data("id");
		$.post("/operation/cfgOperationByRoleId.json", {roleId: id}, function(data) {
			data = JSON.parse(data);
			if(!$("#treeUl").length) {
				$("body").append('<ul id="treeUl" class="ztree" style="display: none;height: 255px;overflow-y: auto;" ></ul>');
			}
			var $html = $("#treeUl"),
				setting = {
					check: {
						enable: true,
						chkboxType: { "Y": "ps", "N": "s" }
					},
					data: {
						
					}
				},
				$tree;
			$tree = $.fn.zTree.init($html, setting, data);
			
			layer.page2(gb.title($btn), $html, {
				area: ['240px', '350px']
			}, function (index, $layer) {
				var checkedObjs = $tree.getCheckedNodes(true),
					oprtds = [], projectIds = [];
				for(var i = 0; i < checkedObjs.length; i++) {
					if(checkedObjs[i].id) {
						oprtds.push(checkedObjs[i].id);
					}
					if(checkedObjs[i].projectId) {
						projectIds.push(checkedObjs[i].projectId);
					}
				}
				$.ajax({
					type: "post",
			        url: "/operation/addOperationsOfRole.json",
			        data: {
						oprtds: oprtds,
						projectIds: projectIds,
						roleId: id
					},
			        submitBtn: $layer.find(".layui-layer-btn0"),
			        sucMsg: "配置权限成功",
			        failMsg: "配置权限失败",
			        suc: function (data) {
			        	layer.close(index);
			        }
			    });
			});
		});
	});
	
});