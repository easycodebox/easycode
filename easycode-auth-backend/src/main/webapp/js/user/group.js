/**
 * 全局属性和方法
 */
utils.extend(window.gb || (window.gb = {}), {
	caches: {
		initTree: false
	},
	init: function() {
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
				data = JSON.parse(data);
				
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
									$parentName.val("").change(); //change是为了主动出发校验
								}else {
									$parentId.val(nodes[0].id);
									$parentName.val(nodes[0].name).change(); //change是为了主动出发校验
								}
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
	$(".handler").UI_switch({
		//操作成功后修改的目标对象
		targetClass: "status",
		url: "/group/openClose.json"
	});
	
	$(".loadBtn").click(function() {
		var $btn = $(this);
		$.post("/group/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.group = data.data;
			layer.page1(gb.title($btn), $('#loadDialogGroup'));
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
	
	$(".updBtn").click(function() {
		var $btn = $(this);
		//初始化树形结构
		gb.initTree();
		$.post("/group/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.group = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'), function() {
				gb.caches.initTree = false;
			});
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
	
	//配置角色
	$(".cfgRolesBtn").click(function() {
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
	
});