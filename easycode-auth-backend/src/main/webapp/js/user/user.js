/**
 * 全局属性和方法
 */
$.extend(true, window.gb || (window.gb = {}), {
	caches: {
		initTree: false
	},
	init: function() {
		//缓存表格操作列html
		this.table.cacheOps();
		//初始化模板
		this.vm = new Vue({
			el: '#tmpls',
			data: {
				user: {}
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
									$groupName = $wrap.find(".groupName"),
									$groupId = $wrap.find(".groupId");
								if(nodes[0].id == "-1") {
									$groupId.val("");
									$groupName.val("").change(); //change是为了主动出发校验
								}else {
									$groupId.val(nodes[0].id);
									$groupName.val(nodes[0].name).change(); //change是为了主动出发校验
								}
								$wrap.find(".tree-tip-div").hide();
							}
						}
				};
				$tree = $.fn.zTree.init($(".ztree"), setting, data);
			});
		}
	},
	existUsername: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		$.ajax({
			async: false,
			type: "POST",
			url: "/user/existUsername.json",
			data: {
				username: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "用户名已被占用";
		else
			return true;
	},
	existNickname: function(val) {
		var exist = false,
			$form = $(this).closest("form"),
			$id = $form.find("input[name=id]");
		$.ajax({
			async: false,
			type: "POST",
			url: "/user/existNickname.json",
			data: {
				nickname: val,
				excludeId: $id.length ? $id.val() : null
			},
			suc: function(data) {
				exist = data.data;
			}
		});
		if(exist)
			return "昵称已被占用";
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
		url: "/user/openClose.json"
	});
	
	$("#data-table").on("click", ".loadBtn", function() {
		var $btn = $(this);
		$.post("/user/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.user = data.data;
			layer.page1(gb.title($btn), $('#loadDialogUser'));
		});
	}).on("click", ".updBtn", function() {
		var $btn = $(this);
		//初始化树形结构
		gb.initTree();
		$.post("/user/load.json",{id: $btn.data("id")}, function(data) {
			gb.vm.user = data.data;
			
			gb.show(gb.title($btn), $('#updDialog'));
		});
	}).on("click", ".cfgRolesBtn", function() {
		//配置角色
		var $btn = $(this),
			userId = $btn.data("id");
		$.post("/role/listByUserId.json", {userId: userId}, function(data) {
			data = data.data;
			var html = '<div id="cfgRolesDiv">',
				templete = '<div style="float:left;margin-left:10px;"><input type="checkbox" id="{0}" name="roleIds" value="{1}" {2} {3} /> <label for="{0}">{4}</label></div>',
				time = new Date().getTime().toString();
			for(var i = 0; i < data.length; i++) {
				html += templete.format(time + i, data[i].id, 
							data[i].isOwn && data[i].isOwn.className == "YES" ? 'checked="checked"' : '',
							data[i].isGroupOwn && data[i].isGroupOwn.className == "YES" ? 'disabled="disabled"' : '',
							data[i].name);
			}
			html += '</div>';
			
			layer.page2(gb.title($btn), html, {
				area: ['300px', '150px']
			}, function (index, $layer) {
				$.ajax({
					url: "/role/cfgByUserId.json",
					data: "userId=" + userId + "&" + $.param($("#cfgRolesDiv input:checked")),
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
		gb.vm.user = {
			status: {
				className: "OPEN"
			},
			sort: 0,
			gender: {
				className: "MALE"
			}
		};
		gb.show(gb.title($btn), $('#addDialog'));
	});
	
	//显示树形结构
	$(".groupName, .tree-tip-div").click(function() {
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