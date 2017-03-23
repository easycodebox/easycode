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
				projects: null,
				permission: {}
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
	permissionValidator: function(val) {
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
						$parentName.val("");
					}else {
						$parentId.val(nodes[0].id);
						$parentName.val(nodes[0].name);
					}
					$form.find(".tree-tip-div").hide();
					//验证选择的树是否有效
					if($form.find("input[name=id]").val())
						$form.data('bValidator').validate($parentName);
				}
			}
		};
		$tree = $.fn.zTree.init($form.find(".ztree"), setting, array);
	},
	listPermissions: function($form, $parentName, projectId) {
		$.post("/permission/listByProject.json", {projectId: projectId}, function(data) {
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
			url: "/permission/existName.json",
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
			url: "/permission/existUrl.json",
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
	
	$("#exportsOper").click(function() {
		gb.initProjects(function() {
			var $export = $("#exportPros");
			if (!$export.data("select2")) {
				$export.select2({
					data: $.map(gb.vm.projects, function(obj) {
						return {id: obj.id, text: obj.name}
					})
				});
			}
			layer.page2("导出", $("#exportDiv"), function() {
				if ($export.val()) {
					location.href = "/permission/exports/" + $export.val();
				} else {
					$.msg("warn", "请选择您要导出权限的项目")
				}
			});
		});
	});
	
	//启用、禁用 功能
	$("#toolbar, #data-table").UI_switch({
		srcSelector: ".handler",//触发事件的对象
		scopeSelector: "#data-table",//操作的dom范围
		targetClass: "status",	//操作成功后修改的目标对象
		url: "/permission/openClose.json"
	});
	
	//导入
	$('#importsOper').fileupload({
    	url: "/permission/imports",
    	singleFileUploads: false,	//所有文件作为一个请求上传至服务端
    	acceptFileTypes: /(\.|\/)xml$/i,
    	messages: {
            acceptFileTypes: '只能上传XML文件'
    	},
    	add: function (e, data) {
    		//重写add函数只要是为了弹出验证失败的信息
    		var $this = $(this);
            if (e.isDefaultPrevented()) {
                return false;
            }
            if (data.autoUpload || (data.autoUpload !== false &&
            		$this.fileupload('option', 'autoUpload'))) {
                data.process(function () {
                    return $this.fileupload('process', data);
                }).done(function () {
                    data.submit();
                }).fail(function () {
                	if (data.files.error) {
                		var info = "";
                		data.files.forEach(function(item) {
                			if (item.error) {
                				info += (info ? "<br>" : "" ) + "【{}】:{}".format(item.name, item.error);
            				}
                		});
                		if (info) {
                			$.msg("warn", info);
            			}
            		}
                });
            }
        }
    }).on("fileuploaddone", function (e, data) {
    	data = data.result;
    	if (data && data.data) {
    		var files = data.data,
    			info = "";
    		files.forEach(function(item, index) {
    			if (item.error) {
    				info += (info ? "<br>" : "" ) + item.name + " : " + item.error;
    			}
    		});
    		if (info) {
    			$.msg("warn", info);
			}
    	} else {
    		$.msg("error", data.msg ? data.msg : "上传文件失败！");
    	}
    }).on("fileuploadfail", function (e, data) {
    	$.msg("error", "上传文件失败！");
    });
	
	//是否是菜单 功能
	$("#toolbar, #data-table").UI_switch({
		srcSelector: ".handlerIsMenu",//触发事件的对象
		scopeSelector: "#data-table",//操作的dom范围
		targetClass: "isMenu",	//操作成功后修改的目标对象
		ajaxKey: "id",
		url: "/permission/changeIsMenu.json",
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
		$.post("/permission/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.permission = data.data;
			layer.page1(gb.title($btn), $('#loadDialogPermission'));
		});
	}).on("click", ".updBtn", function() {
		var $btn = $(this),
			id = $btn.data("id");
		gb.initProjects(function() {
			$.post("/permission/load.json",{id: id}, function(data) {
				gb.vm.permission = data.data;
				
				var $form = $('#updDialog');
				if(data.data.projectId) {
					gb.listPermissions($form, $form.find(".parentName"), data.data.projectId);
				}
				gb.show(gb.title($btn), $form);
			});
		});
	});
	
	$("#addBtn").click(function() {
		var $btn = $(this);
		gb.initProjects(function() {
			//初始化选项默认值
			gb.vm.permission = {
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
			gb.listPermissions($form, $parentName, val);
		}else {
			gb.refreshTree($form, $parentName, false);
		}
	});
	
});