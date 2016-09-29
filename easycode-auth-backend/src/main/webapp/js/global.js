/**
 * @author WangXiaoJin
 */
//全局属性和方法
window.gb = {
	caches: {},
	/**
	 * bootstrap-table插件的功能，主要存放一些格式化函数
	 */
	table: {
		/**
		 * 表单查询数据时传递的参数
		 */
		params: function(params) {
			var jsonParams = this.queryForm ? $("#" + this.queryForm).serializeJSON() : {};
			jsonParams.pageNo = params.pageNumber;
			jsonParams.pageSize = params.pageSize;
			if (params.sortName) {
				jsonParams.sortName = params.sortName;
				jsonParams.sortOrder = params.sortOrder;
			}
			return jsonParams;
		},
		/**
		 * 缓存表格的操作列html内容
		 */
		cacheOps: function() {
			var $ops = $("#table-ops");
			gb.caches.tableOps = $ops.html();
			$ops.remove();
		},
		/**
		 * 表格操作列
		 */
		fmtOps: function(value, row, index) {
			return gb.caches.tableOps.format(row);
		},
		/**
		 * 序号
		 */
		fmtOrder: function(value, row, index) {
			return ++index;
		},
		/**
		 * 图片
		 */
		fmtPic: function(value, row, index) {
			return value && utils.imgUrlFormat(value, "r40c40");
		},
		/**
		 * 格式化 开启/关闭 按钮
		 */
		fmtOpenClose: function(value, row, index) {
			if (value.className == "OPEN") {
				return '<div class="handler switch-close yes status" />';
			} else if (value.className == "CLOSE") {
				return '<div class="handler switch-open no status" />';
			}
		},
		/**
		 * 格式化日期
		 */
		fmtDate: function(value, row, index) {
			return utils.fmtDate(value);
		}
	},
	/**
	 * 清空form表单的数据,如果想不清空指定的对象，则加上class="not-reset"
	 */
	resetForm: function($form) {
		if($form.length == 0) return false;
		/*//清空UI_select的值
		$form.find(".ui-select-value:not(.not-reset)").each(function(){
			$.UI_select.unselect($(this).attr("id"), $(this).val());
		});*/
		$form.find("input[type=text]:not(.not-reset),input[type=hidden]:not(.not-reset),input[type=file]:not(.not-reset),input[type=password]:not(.not-reset),textarea:not(.not-reset)").val("");
		$form.find("input:checked:not(.not-reset)").prop("checked",false);
		$form.find("select:not(.not-reset)").each(function() {
			this.options.selectedIndex = 0;
		});
		if(utils.browser.ie) {
			$form.find("input[type=file]:not(.not-reset)").each(function() {
				var self = $(this);
				self.after(self.clone().val(""));
				self.remove();
			});
		}
		if($.uploadImg) {
			//清除上传图片内容
			$.uploadImg.clear($form);
		}
	},
	/**
	 * 获取dom对象的title值，取值规则 title --> value --> text
	 * @param $obj
	 */
	title: function($obj) {
		var title;
		return (title = $obj.attr("title")) ? title : (title = $obj.val()) ? title : $obj.text();
	},
	/**
	 * 显示新增、修改等弹出框
	 * @param title 弹出框标题
	 * @param $dialog 弹出框
	 * @param success 新增成功执行的函数
	 */
	show: function(title, $dialog, success) {
		var $form = $dialog.is("form") ? $dialog : $dialog.find("form"),
			valid = $form.data("bValidator");
		if($form.length == 0) {
			$.msg("error", "弹出框内缺少form标签");
			return false;
		}
		
		layer.page2(title, $dialog, {
			success: function($layer, index) {
				//清空form验证提示信息
				valid.reset();
			}
		}, function(index, $layer) {
			if(valid.validate()) {
				$.ajax({
					type: "post",
			        url: $form.attr("action"),
			        data: $form.serializeJSON(),
			        submitBtn: $layer.find(".layui-layer-btn0"),
			        sucMsg: $form.data("suc"),
			        failMsg: $form.data("fail"),
			        suc: function (data) {
			        	layer.close(index);
			        	if(utils.isFunction(success)) {
			        		success.apply(this, arguments);
			        	}
			        	$("#data-table").bootstrapTable("refresh");
			        }
			    });
			}
		});
	},
	/**
	 * 删除
	 * _info 	删除确认框的提示信息
	 * _sucMsg	删除成功后的提示信息
	 * _failMsg 删除失败后的提示信息
	 */
	remove: function(url, id, sucFun) {
		var opts = {
				info: "确定删除？",
				url: null,
				target: "#data-table input[name=ids]:checked",
				ids: null,
				sucMsg: "删除成功!",
				failMsg: "删除失败!",
				sucFun: null,
				refresh: function(data, textStatus, jqXHR) {
					$("#data-table").bootstrapTable("refresh");
				}
		};
		if (utils.isObject(url)) {
			$.extend(opts, url);
		} else {
			opts.url = url;
			if (utils.isFunction(id)) {
				opts.sucFun = id;
			} else {
				opts.ids = id;
				opts.sucFun = sucFun;
			}
		}
		if (!opts.url) {
			$.msg("warn", "url参数不能为null");
			return;
		}
		if (!opts.ids) {
			if (!opts.target) {
				$.msg("warn", "target参数不能为null");
				return;
			}
			var $target = $(opts.target);
			if(!$target.length) {
				$.msg("warn", "请先选择您要删除的对象！");
				return;
			}
			opts.ids = [];
			$target.each(function() {
				opts.ids.push($(this).val());
			});
		} else if (utils.isArray(opts.ids)) {
			opts.ids = [opts.ids];
		}
		$.confirm(opts.info, function() {
			$.ajax({
	            type: "post",
	            url: opts.url,
	            data: {ids: opts.ids},
	            sucMsg: opts.sucMsg,
	            failMsg: opts.failMsg,
	            suc: function (data, textStatus, jqXHR) {
	    			if(utils.isFunction(opts.sucFun))
	    				opts.sucFun(data, textStatus, jqXHR);
	    			if(utils.isFunction(opts.refresh))
	    				opts.refresh(data, textStatus, jqXHR);
	    			
	            }
	        });
		});
	}
};
/**
 * 页面初始化
 */
$(function() {
	
	/************  左侧菜单  *****************/
	(function() {
		$(".treeview").off(".frame").on("click.frame", function() {
			var $this = $(this),
				url = $this.children("a").attr("href");
			if (!url || url == "#" || url.startsWith("javascript:")) {
				return;
			}
			var menuId = $this.data("menuId"),
				memus = localStorage[BaseData.menus] ? JSON.parse(localStorage[BaseData.menus]) : {};
			memus[BaseData.path] = menuId;
			localStorage[BaseData.menus] = JSON.stringify(memus);
		});
		//初始化菜单的显示
		var memus = localStorage[BaseData.menus] ? JSON.parse(localStorage[BaseData.menus]) : {};
			menuId = memus[BaseData.path];
		if (menuId) {
			$(".treeview[data-menu-id=" + menuId + "]").parents(".treeview").andSelf().addClass("active");
		}
	})();
	
	/************  右侧面板菜单  *****************/
	(function(AdminLTE) {
		function changeLayout(cls) {
			$("body").toggleClass(cls);
			AdminLTE.layout.fixSidebar();
			//Fix the problem with right sidebar and layout boxed
			if (cls == "layout-boxed")
				AdminLTE.controlSidebar._fix($(".control-sidebar-bg"));
			if ($("body").hasClass("fixed") && cls == "fixed") {
				AdminLTE.pushMenu.expandOnHover();
				AdminLTE.layout.activate();
			}
			//触发布局事件
			$("body").trigger("layout.frame");
			AdminLTE.controlSidebar._fix($(".control-sidebar-bg"));
			AdminLTE.controlSidebar._fix($(".control-sidebar"));
		}

		function changeSkin(cls) {
			var $body = $("body"),
				rawClass = $body.attr("class").replace(/skin-\w+/g, "");
			$body.attr("class", rawClass + " " + cls);
			localStorage.setItem("skin", cls);
		}
		
		var $body = $("body"),
			tmp = localStorage.getItem("skin");
		if (tmp) changeSkin(tmp);

		$("[data-skin]").off(".frame").on("click.frame", function(e) {
			if ($(this).hasClass("knob"))
				return;
			e.preventDefault();
			changeSkin($(this).data("skin"));
		});

		$("[data-layout]").off(".frame").on("click.frame", function() {
			changeLayout($(this).data("layout"));
		});

		$("[data-controlsidebar]").off(".frame").on("click.frame", function() {
			changeLayout($(this).data("controlsidebar"));
			var slide = !AdminLTE.options.controlSidebarOptions.slide;
			AdminLTE.options.controlSidebarOptions.slide = slide;
			if (!slide)
				$(".control-sidebar").removeClass("control-sidebar-open");
		});

		$("[data-sidebarskin='toggle']").off(".frame").on("click.frame", function() {
			var sidebar = $(".control-sidebar");
			if (sidebar.hasClass("control-sidebar-dark")) {
				sidebar.removeClass("control-sidebar-dark")
				sidebar.addClass("control-sidebar-light")
			} else {
				sidebar.removeClass("control-sidebar-light")
				sidebar.addClass("control-sidebar-dark")
			}
		});

		$("[data-enable='expandOnHover']").off(".frame").on("click.frame", function() {
			$(this).attr('disabled', true);
			AdminLTE.pushMenu.expandOnHover();
			if (!$('body').hasClass('sidebar-collapse'))
				$("[data-layout='sidebar-collapse']").click();
		});

		// Reset options
		if ($body.hasClass('fixed')) {
			$("[data-layout='fixed']").attr('checked', 'checked');
		}
		if ($body.hasClass('layout-boxed')) {
			$("[data-layout='layout-boxed']").attr('checked', 'checked');
		}
		if ($body.hasClass('sidebar-collapse')) {
			$("[data-layout='sidebar-collapse']").attr('checked', 'checked');
		}
	})($.AdminLTE);
	
	/************  搜索区域  *****************/
	//初始化搜索区域
	(function() {
		var dowm = 'glyphicon-chevron-down',
        	up = 'glyphicon-chevron-up';
		//当宽度改变时，重置表单项的显示/影藏状态
		window.gb.resetFormGroupStatus = function($form) {
			var $form = $form || $(".search"),
				hidable = 'hidable';
			//显示小标签，为了计算宽度时包含它
			$form.children(".form-btns").children(".toggle-icon").show();
			$form.has(".toggle-icon").each(function() {
				var $this = $(this),
					$formGroups = $this.children(".form-group"),
					$formBtns = $this.children(".form-btns"),
					$funBtns = $this.children(".fun-btns"),
					usableWidth = $form.width() - $formBtns.outerWidth(true) - $funBtns.outerWidth(true),
					hasHide = false,
					sum = 0;
				for (var i = 0; i < $formGroups.length; i++) {
					var $fg = $($formGroups[i]);
					if (!hasHide) {
						sum += $fg.outerWidth(true);
						if (sum > usableWidth) {
							hasHide = true;
							$fg.before($formBtns).addClass(hidable).hide();
							if ($funBtns.length) {
								$fg.before($funBtns);
							}
						} else {
							$fg.removeClass(hidable).show();
						}
					} else {
						$fg.addClass(hidable).hide();
					}
				}
				if (!hasHide) {
					$formBtns.children(".toggle-icon").hide();
				}
			});
		};
		
		//当浏览器窗口改变时，重置搜索区域选项显示/影藏状态
        if(!utils.bindedEvent($(window), "resize", "frame")) {
        	$(window).off(".frame").on("resize.frame", utils.debounce(gb.resetFormGroupStatus));
        }
        //当左侧菜单项展开/收缩时，重置搜索区域选项显示/影藏状态【AdminLTE.js触发此事件】
        $("body").off(".pushMenu").on("expanded.pushMenu collapsed.pushMenu", function() {
        	//因为左侧菜单显示/影藏有一个滑动效果，所以0.3s后计算执行逻辑
        	setTimeout(function() {
        		gb.resetFormGroupStatus();
        	}, 300);
        });
        //右侧控制面板中修改页面布局
        $("body").off("layout.frame").on("layout.frame", function() {
        	//因为左侧菜单显示/影藏有一个滑动效果，所以0.3s后计算执行逻辑
        	setTimeout(function() {
        		gb.resetFormGroupStatus();
        	}, 300);
        });
        
		//展开、收起搜索功能区
        $(".toggle-icon").each(function() {
            var $this = $(this),
                $form = $this.closest(".search"),
                $content = $('<span class="glyphicon {0} toggle-icon" title="展开" aria-hidden="true"></span>'.format(dowm));
            if ($form.length == 0) {
                if (console)
                    console.error("展开/收起", "功能需要提供.search对象");
                return;
            }
            $this.replaceWith($content);
            gb.resetFormGroupStatus($form);
            
            $content.off(".frame").on("click.frame", function() {
                var $this = $(this);
                if ($this.hasClass(dowm)) {
                    $this.removeClass(dowm).addClass(up).attr("title", "收起");
                    $form.children(".hidable").show();
                } else {
                    $this.removeClass(up).addClass(dowm).attr("title", "展开");
                    $form.children(".hidable").hide();
                }
            });
        });
	})();
	
	/************  绑定事件  *****************/
	(function() {
        
		//清空form表单的数据,如果想不清空指定的对象，则加上class="not-reset"
		$(".reset-btn").off(".frame").on("click.frame", function() {
			gb.resetForm($(this).closest("form"));
			return false;
		});
		
		//修改密码
		$("#updPwdBtn").off(".frame").on("click.frame", function() {
			var $btn = $(this),
				$dialog = $("#updPwdDialog");
			if($dialog.length) {
				//重置form表单
				gb.resetForm($dialog);
				gb.show(gb.title($btn), $dialog);
			}else {
				$.get("/user/updatePwd.html", function(data) {
					$("body").append($dialog = $(data));
					//绑定表单验证
					$dialog.bValidator();
					
					gb.show(gb.title($btn), $dialog);
				});
			}
		});
		
		//绑定表单验证
		$(".form-validate").bValidator();
		
		//绑定搜索区域的查询按钮
		$(".search").off(".frame").on("submit.frame", function() {
			var id = $(this).attr("id");
			if (id) {
				$("table[data-query-form={}]".format(id)).bootstrapTable("refresh");
			}
			return false;
		});
		
	})();
	
});

;(function($){
	
	/*******************************  配置ajax  **************************************/
	$.ajaxSetup({
    	traditional: true, //Jquery ajax请求时，用传统方式组装参数。设置此值后，参数不能传嵌套数组
    });
	
	/*******************************  table  **************************************/
	$.extend($.fn.bootstrapTable.defaults, {
		striped: true,
		showToggle: true,
		//search: true,		//显示搜索框
		showRefresh: true,	//显示刷新按钮
		detailView: true,	//显示详情按钮，最前面的“+”符号
		detailFormatter: function(index, row) {
			var opts = this, html = [];
			function showField(columns) {
				if (utils.isArray(columns)) {
					for (var i = 0; i < columns.length; i++) {
						var col = columns[i];
						if (utils.isArray(col)) {
							showField(col);
						} else {
							if (col.visible && col.title && utils.isString(col.field)) {
								var val = row[col.field], processed = false;
								//判断是否是Object类型，且为枚举，如果是枚举则显示desc属性
								if (utils.isObject(val) && val.desc) {
									val = val.desc;
									processed = true;
								}
								//判断是否有格式化函数
								if (!processed && col.formatter) {
									var func = eval('(' + col.formatter + ')');
									if (utils.isFunction(func)) {
										val = func.call(col, val, row, index);
									}
								}
								html.push('<p><b>' + col.title + ':</b> ' + (val || opts.undefinedText) + '</p>');
							}
						}
					}
				}
			};
			showField(opts.columns);
	        return html.join("");
		},
		showColumns: true,	//控制table是否显示某列column的按钮
		method: "post",		//ajax请求类型
		//cache: false,		//禁用ajax缓存
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		pagination: true,
		//pageList: [10, 25, 50, "ALL"],//ALL显示所有
		sidePagination: "server",//服务端分页
		queryParamsType: "common",//取消默认的'limit'类型
		responseHandler: function(res) {
			//格式化服务返回数据。插件需要返回的数据格式：{total: 100, rows: [], fixedScroll: true(可选)}
			if (res && (!res.code || BaseData.suc === res.code) && res.data) {
				var total = res.totalCount || res.data.totalCount,
					rows = res.data.data || res.data;
				return {total: total, rows: rows};
			} else {
				return {total: 0, rows: []};
			}
		},
		//selectItemName: "ids",//radio或checkbox的name属性值
		clickToSelect: true
	});
	$.extend($.fn.bootstrapTable.columnDefaults, {
		align: "center",
		valign: "middle",
		clickToSelect: false
	});
	
	/*******************************  配置layer  **************************************/
	layer.config({
		maxWidth: "auto" //默认的maxWidth为360，固改成auto
	});
	//一个按钮【确认】
	layer.page1 = function(title, content, options, yes) {
		var type = typeof options === 'function';
	    if(type) yes = options;
		return layer.open($.extend({
			id: "pageDialog",
			type: 1,
			title: title,
			content: content,
			btn: ['确认'],
			yes: yes
		}, type ? {} : options));
	}
	//两个按钮【确认】【取消】
	layer.page2 = function(title, content, options, yes, cancel) {
		var type = typeof options === 'function';
		if (type) {
			cancel = yes;
			yes = options;
		}
		return layer.open($.extend({
			id: "pageDialog",
			type: 1,
			title: title,
			content: content,
			btn: ['确认', '取消'],
			yes: yes,
			btn2: cancel
		}, type ? {} : options));
	}
	
	/****************************** 扩展实现utils.js功能 *************************************/
	//icon值转换
	var convertIcon = function(icon) {
		if (icon != null && utils.isString(icon)) {
			switch (icon) {
			case "info":
				icon = 1;
				break;
			case "warn":
				icon = 0;
				break;
			case "error":
				icon = 2;
				break;
			default:
				break;
			}
		}
		return icon;
	};
	$.extend({
		/**
		 * 组合格式：
		 * icon, msg, fn
		 * msg, fn
		 * msg
		 * icon = warn/info/error
		 */
		alert: function(icon, msg, fn) {
			if (utils.isFunction(msg)) {
				fn = msg;
				msg = icon;
				icon = undefined;
			}
			if (msg == null) {
				msg = icon;
				icon = undefined;
			}
			icon = convertIcon(icon);
			if (utils.isFunction(fn)) {
				var tmp = fn;
				fn = function(index) {
					tmp.apply(this, arguments);
					layer.close(index);
				};
			}
			return layer.alert(msg, {icon: icon}, fn);
		},
		/**
		 * 组合格式：
		 * icon, msg, delay, fn
		 * icon, msg, delay
		 * icon, msg, fn
		 * msg, delay, fn
		 * msg, fn
		 * msg, delay
		 * icon, msg
		 * msg
		 */
		msg: function(icon, msg, delay, fn) {
			if(utils.isFunction(delay)) {
				fn = delay;
				if(utils.isNumber(msg)) {
					delay = msg;
					msg = icon;
					icon = undefined;
				} else {
					delay = undefined;
				}
			}
			if(utils.isFunction(msg)) {
				fn = msg;
				delay = undefined;
				msg = icon;
				icon = undefined;
			}
			if(utils.isNumber(msg)) {
				delay = msg;
				msg = icon;
				icon = undefined;
			}
			if(msg == null) {
				msg = icon;
				icon = undefined;
			}
			icon = convertIcon(icon);
			return layer.msg(msg, {
				icon: icon,
				time: delay
			}, fn);
		},
		confirm: function(content, yes, no) {
			if (utils.isFunction(yes)) {
				var tmp = yes;
				yes = function(index) {
					tmp.apply(this, arguments);
					layer.close(index);
				};
			}
			return layer.confirm(content, {
				icon: 3,
				title: '提示'
			}, yes, no);
		}
	});
	
	/*******************************  扩展Vue  **************************************/
	//格式化日期
	Vue.filter('date', utils.fmtDate);
	//格式化图片
	Vue.filter('img', function(value, rule, mode) {
		if(value && rule) {
			value = utils.imgUrlFormat(value, rule, mode);
			return value ? BaseData.imgUrl + "/" + value : value;
		}
		return value;
	});
	
})(jQuery);
