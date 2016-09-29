/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	caches: {
		
	},
	table: {
		/**
		 * 格式化 是否是菜单
		 */
		fmtIsMenu: function(value, row, index) {
			if (value.className == "YES") {
				return '<div class="handlerIsMenu switch-close yes isMenu" />';
			} else if (value.className == "NO") {
				return '<div class="handlerIsMenu switch-open no isMenu" />';
			}
		}
	},
	init: function() {
		//缓存表格操作列html
		this.table.cacheOps();
		//初始化模板
		this.vm = new Vue({
			el: '#tmpls',
			data: {
				projects: null,
				operation: {}
			}
		});
	},
	//初始化项目下拉框
	initProjects: function(callback) {
		if(gb.vm.projects) {
			if(utils.isFunction(callback)) {
				callback();
			}
		}else {
			$.get("/project/all.json", function(data) {
				gb.vm.projects = data.data;
				if(utils.isFunction(callback)) {
					callback();
				}
			});
		}
	},
	operationValidateTime: function(validator) {
		var $self = $(this);
		$self.focus(function() {
			validator.validate(false, $self);
		});
	},
	operationValidator: function(val) {
		var $form = $(this).closest("form");
		if(!$form.find(".projectId").val()) {
			return "请先选择项目";
		}
		var $id = $form.find("input[name=id]");
		if($id.length && $form.find(".parentId").val() == $id.val()) {
			return "上级权限不能是自身";
		}
		return true;
	},
	refreshTree: function($form, $parentName, array) {
		if(array !== false) {
			if(!array || array.length == 0)
				$parentName.val("没有权限可供选择");
		}else
			array = [];
		var $tree, setting = {
			view: {
				selectedMulti: false
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				onClick: function onClick(e, treeId, treeNode) {
					e.stopPropagation();
					var zTree = $.fn.zTree.getZTreeObj(treeId),
						nodes = zTree.getSelectedNodes(),
						$parentId = $form.find(".parentId");
					if(nodes[0].id == "-1") {
						$parentId.val("");
						$parentName.val("").change(); //change是为了主动出发校验
					}else {
						$parentId.val(nodes[0].id);
						$parentName.val(nodes[0].name).change(); //change是为了主动出发校验
					}
					$form.find(".tree-tip-div").hide();
					//验证选择的树是否有效
					if($form.find("input[name=id]").val())
						$form.data('bValidator').validate(false, $parentName);
				}
			}
		};
		$tree = $.fn.zTree.init($form.find(".ztree"), setting, array);
	},
	listOperations: function($form, $parentName, projectId) {
		$.post("/operation/listByProject.json", {projectId: projectId}, function(data) {
			data = JSON.parse(data);
			gb.refreshTree($form, $parentName, data);
		});
	},
	existName: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		if(!$form.find(".projectId").val()) {
			return "请先选择项目";
		}
		$.ajax({
			async: false,
			type: "POST",
			url: "/operation/existName.json",
			data: {
				projectId: projectId,
				name: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "权限名已被占用";
		else
			return true;
	},
	existUrl: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			projectId = $form.find(".projectId").val(),
			$id = $form.find("input[name=id]");
		if(!projectId) {
			return "请先选择项目";
		}
		$.ajax({
			async: false,
			type: "POST",
			url: "/operation/existUrl.json",
			data: {
				projectId: projectId,
				url: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "url已被占用";
		else
			return true;
	}
});
$(function(){
	
	//初始化页面
	gb.init();
	
	$("#importsOper").click(function() {
		$.post("/operation/imports.json");
	});
	
	$("#exportsOper").click(function() {
		$.post("/operation/exports.json");
	});
	
	//启用、禁用 功能
	$("#toolbar, #data-table").UI_switch({
		srcSelector: ".handler",//触发事件的对象
		scopeSelector: "#data-table",//操作的dom范围
		targetClass: "status",	//操作成功后修改的目标对象
		url: "/operation/openClose.json"
	});
	
	//是否是菜单 功能
	$("#toolbar, #data-table").UI_switch({
		srcSelector: ".handlerIsMenu",//触发事件的对象
		scopeSelector: "#data-table",//操作的dom范围
		targetClass: "isMenu",	//操作成功后修改的目标对象
		url: "/operation/changeIsMenu.json",
		change: {
			"switch-open": {
				confirmMsg	: "设置为菜单？",
		 		failMsg		: "设置为菜单失败!",
		 		data: {isMenu: "YES"}
			},
			"switch-close": {
				confirmMsg	: "设置为'非'菜单？",
		 		failMsg		: "设置为'非'菜单失败!",
		 		data: {isMenu: "NO"}
			}
		}
	});
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/operation/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.operation = data.data;
			layer.page1(gb.title($btn), $('#loadDialogOperation'));
		});
	}).on("click", ".updBtn", function() {
		var $btn = $(this),
			id = $btn.data("id");
		gb.initProjects(function() {
			$.post("/operation/load.json",{id: id}, function(data) {
				gb.vm.operation = data.data;
				
				var $form = $('#updDialog');
				if(data.data.projectId) {
					gb.listOperations($form, $form.find(".parentName"), data.data.projectId);
				}
				gb.show(gb.title($btn), $form);
			});
		});
	});
	
	$("#addBtn").click(function() {
		var $btn = $(this);
		gb.initProjects(function() {
			//初始化选项默认值
			gb.vm.operation = {
					status: {
						className: "OPEN"
					},
					isMenu: {
						className: "NO"
					},
					sort: 0
			};
			
			gb.show(gb.title($btn), $('#addDialog'));
		});
	});
	
	//显示树形结构
	$(".parentName, .tree-tip-div").click(function() {
		var $treeDiv = $(this).closest(".tree-tip-wrap").find(".tree-tip-div");
		if($treeDiv.is(":hidden")) {
			$treeDiv.show();
			$("body").one("click", function(){
				$treeDiv.hide();
			});
		}
		return false;
	});
	
	$(".projectId").change(function() {
		var val = $(this).val(),
			$form = $(this).closest("form"),
			$parentName = $form.find(".parentName"),
			valid = $form.data('bValidator');
		$parentName.val("");
		$form.find(".parentId").val("");
		valid.reset($parentName);
		valid.reset($form.find("input[name=url]"));
		if(val) {
			gb.listOperations($form, $parentName, val);
		}else {
			gb.refreshTree($form, $parentName, false);
		}
	});
	
});