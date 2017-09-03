/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	caches: {
		initTree: false
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
				group: {}
			}
		});
	},
	//初始化树形结构
	initTree: function() {
		if(!this.caches.initTree) {
			$.post("/group/listAll.json", function(data) {
				gb.caches.initTree = true;
				//data = JSON.parse(data);
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
									$wrap = $(e.currentTarget).closest(".tree-tip-wrap"),
									$parentName = $wrap.find(".parentName"),
									$parentId = $wrap.find(".parentId");
								if(nodes[0].id == "-1") {
									$parentId.val("");
									$parentName.val("");
								}else {
									$parentId.val(nodes[0].id);
									$parentName.val(nodes[0].name);
								}
								$wrap.closest("form").data('bValidator').validate($parentName);
								$wrap.find(".tree-tip-div").hide();
							}
						}
				};
				$tree = $.fn.zTree.init($(".ztree"), setting, data);
			});
		}
	},
	existName: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		$.ajax({
			dataType: "json", 
			async: false,
			type: "POST",
			url: "/group/existName.json",
			data: {
				name: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "组名已被占用";
		else
			return true;
	},
	parentValidator: function(val) {
		var $form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		if($id.length && $form.find(".parentId").val() == $id.val()) {
			return "上级组织不能选择自身";
		}
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
		url: "/group/openClose.json"
	});
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/group/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.group = data.data;
			layer.page1(gb.title($btn), $('#loadDialogGroup'));
		});
	}).on("click", ".updBtn", function() {
		var $btn = $(this);
		//初始化树形结构
		gb.initTree();
		$.post("/group/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.group = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'), function() {
				gb.caches.initTree = false;
			});
		});
	}).on("click", ".cfgRolesBtn", function() {
		//配置角色
		var $btn = $(this),
			id = $btn.data("id");
		$.post("/role/listByGroupId.json", {groupId: id}, function(data) {
			data = data.data;
			var html = '<div id="cfgRolesDiv">',
				templete = '<div style="float:left;margin: 0 8px;"><input type="checkbox" id="{0}" name="roleIds" value="{1}" {2} /> <label for="{0}">{3}</label></div>',
				time = new Date().getTime().toString();
			for(var i = 0; i < data.length; i++) {
				html += templete.format(time + i, data[i].id, 
							data[i].isOwn && data[i].isOwn.className == "YES" ? 'checked="checked"' : '',
							data[i].name);
			}
			html += '</div>';
			layer.page2(gb.title($btn), html, {
				area: ['300px', '200px']
			}, function(index, $layer) {
				$.ajax({
					url: "/role/cfgByGroupId.json",
					data: "groupId=" + id + "&" + $.param($("#cfgRolesDiv input:checked")),
			        submitBtn: $layer.find(".layui-layer-btn0"),
			        sucMsg: "配置角色成功",
			        failMsg: "配置角色失败",
			        suc: function (data) {
			        	layer.close(index);
			        }
			    });
			});
		});
	});
	
	$("#addBtn").click(function() {
		var $btn = $(this);
		//初始化树形结构
		gb.initTree();
		//初始化选项默认值
		gb.vm.group = {
			status: {
				className: "OPEN"
			},
			sort: 0
		};
		gb.show(gb.title($btn), $('#addDialog'), function() {
			gb.caches.initTree = false;
		});
	});
	
	//显示树形结构
	$(".parentName, .tree-tip-div").click(function() {
		var $treeDiv = $(this).closest(".tree-tip-wrap").find(".tree-tip-div");
		if($treeDiv.is(":hidden")) {
			$treeDiv.show();
			$("body").one("click", function() {
				$treeDiv.hide();
			});
		}
		return false;
	});
	
});