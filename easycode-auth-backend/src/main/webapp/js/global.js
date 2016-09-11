/**
 * @author WangXiaoJin
 */
//全局属性和方法
window.gb = {
	caches: {},
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
			valid = $form.data('bValidator');
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
			        	//等替换表格插件后需要修改 - 待修改
			        	window.location.reload(true);
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
	remove: function(_url, _id, _info, _sucMsg, _failMsg, _sucFun) {
		if(utils.isFunction(_info)){
			_sucFun = _info;
			_info = null;
			_sucMsg = null;
			_failMsg = null;
		}else if(utils.isFunction(_sucMsg)){
			_sucFun = _sucMsg;
			_sucMsg = null;
			_failMsg = null;
		}
		$.confirm(_info || "确定删除？", function(){
			$.ajax({
	            type: "post",
	            url: _url,
	            data: {ids:_id},
	            sucMsg: _sucMsg || "删除成功!",
	            failMsg: _failMsg || "删除失败!",
	            suc: function (data, textStatus, jqXHR) {
					//删除指定的行
					$("tr[trid='"+_id+"']").remove();
					//显示的总数减1
					if($(".content").length > 0) {
						$(".totalCount").text($(".totalCount").text()-1);
					}
					//重新排序
					$.UI_table.resortRowNo();
	    			if(utils.isFunction(_sucFun))
	    				_sucFun(data, textStatus, jqXHR);
	            }
	        });
		});
	},
	/**
	 * 批量删除
	 * _info 	删除确认框的提示信息
	 * _sucMsg	删除成功后的提示信息
	 * _failMsg 删除失败后的提示信息
	 * _sucFun  回调函数
	 * 只有在_sucFun返回值不等于false ，且ajax请求返回suc 时才会刷新页面
	 */
	batchRemove: function(_url, _info, _sucMsg, _failMsg, _sucFun) {
		if(utils.isFunction(_info)){
			_sucFun = _info;
			_info = null;
			_sucMsg = null;
			_failMsg = null;
		}else if(utils.isFunction(_sucMsg)){
			_sucFun = _sucMsg;
			_sucMsg = null;
			_failMsg = null;
		}
		var idsDom = $("input[name=ids]:checked");
		if(idsDom.length === 0){
			$.msg("warn", "请先选择您要删除的对象！");
			return;
		}
		$.confirm(_info || idsDom.length == 1 ? "确定删除？" : "确定批量删除？", function(){
			$.ajax({
	            type: "post",
	            url: _url,
	            data: $("input[name=ids]:checked"),
	            sucMsg: _sucMsg || idsDom.length == 1 ? "删除成功!" : "批量删除成功!", 
	            failMsg: _failMsg || idsDom.length == 1 ? "删除失败!" : "批量删除失败!",
	            suc: function(data, textStatus, jqXHR){
	    			var reload = true;
	    			if(utils.isFunction(_sucFun))
	    				reload = _sucFun(data, textStatus, jqXHR);
	    			if(reload !== false) {
	    				//等替换表格插件后需要修改 - 待修改
			        	window.location.reload(true);
	    			}
	    		}
	        });
		});
	}
};
/**
 * 页面初始化
 */
$(function() {
	
	//渲染页面
	window.renderPage = function() {
		
		$("table.ui-table").UI_table();
		
		if(!utils.bindedEvent($(window), 'resize', "frame")) {
			//设置主题结构的宽、高
			var resizeContent = function(){
				
				var $main = $('#main'),
					$header = $("#header"),
					$conent = $("#conent"),
					$menus = $("#menus"),
					$coreDiv = $("#core-div"),
					$control = $(".control"),
					$search = $("ul.search"),
					$paramLi = $search.children(".param-li"),
					$dataDiv = $(".data-div"),
					$page = $(".page"),
					$footer = $("#footer");
				
				$main.height($(window).height() - $header.outerHeight(true));
				$conent.outerWidth($(window).width() - $menus.outerWidth(true));
				
				if($coreDiv.length == 0) return;
				
				$coreDiv.width($conent.width() - ($coreDiv.outerWidth(true) - $coreDiv.width()));
				$control.width($coreDiv.width() - ($control.outerWidth(true) - $control.width()));
				
				//计算查询条件的显示/影藏
				var searchWidth = $control.width() - $(".search-btns").outerWidth(true) - ($(".handle-btns").outerWidth(true) || 0),
					hideParam = false;
				$paramLi.each(function(){
					var hide = true;
					if(searchWidth > 0) {
						searchWidth -= $(this).outerWidth(true);
						hide = searchWidth < 0 ? true : false;
					}
					if(hide) {
						$(this).addClass("param-hidden").attr("param-hidden", "true");
						hideParam = true;
					}else
						$(this).removeClass("param-hidden").removeAttr("param-hidden");
				});
				$search.width("100%");
				if(hideParam) {
					var hiding = true;
					$paramLi.off(".frame").on("mouseenter.frame", function(){
						if(hiding) {
							$(".param-li[param-hidden]").removeClass("param-hidden");
							$(".control .handle-btns").hide();
							$control.addClass("border-bottom");
							hiding = false;
						}
					});
					$control.off(".frame").on({
						"mouseleave.frame": function() {
							if(!hiding) {
								$(".param-li[param-hidden]").addClass("param-hidden");
								$(".control .handle-btns").show();
								$control.removeClass("border-bottom");
								hiding = true;
							}
						}
					});
				}else {
					$control.off(".frame");
				}
				
				var coreDivExtra = $coreDiv.outerHeight(true) - $coreDiv.height(),
					shiftyHeight = ($page.position() || $footer.position()).top - $coreDiv.position().top - coreDivExtra,
					dataHeight = shiftyHeight - ($dataDiv.position() ? $dataDiv.position().top : 0);
				$coreDiv.height(shiftyHeight);
				$dataDiv.outerWidth($coreDiv.width()).outerHeight(dataHeight);
				$.UI_table.resize($("table.ui-table"), $coreDiv.width(), dataHeight);
			};
			resizeContent();
			//设置主题结构的宽、高
			$(window).off(".frame").on("resize.frame", utils.debounce(resizeContent));
		}
		
	};
	
	/************  左侧菜单  *****************/
	(function() {
		$('.one').off(".init-page").on("click.init-page", function() {
			var $this = $(this),
				$two = $this.next().children(".two");
			if($two.hasClass('show')) {
				$this.find(".icon").css(
					'background-image',
					'url(/imgs/frame/menu_li_bg.png)');
				$two.removeClass('show').addClass('hidden');
			}else {
				$(".one .icon").css(
						'background-image',
						'url(/imgs/frame/menu_li_bg.png)');
				$this.find(".icon").css(
						'background-image',
						'url(/imgs/frame/menu_li_bg_down.png)');
				$('.two').removeClass('show').addClass('hidden');
				$two.removeClass('hidden').addClass('show');
			}
		});
		$(".menu-bar").hover(
			function() {
				$(this).find("a").addClass("menu-hover");
			},
			function() {
				$(this).find("a").removeClass("menu-hover");
			}
		).click(function(){
			$(".menu-bar").find("a").removeClass("selected");
			$(this).find("a").addClass("selected");
			var menuId = $(this).attr("menu-id"),
				memus = $.cookie(BaseData.menus) ? JSON.parse($.cookie(BaseData.menus)) : {};
			memus[BaseData.path] = menuId;
			$.cookie(BaseData.menus, JSON.stringify(memus), {path: '/'});
		});
		//初始化菜单的显示
		var memus = $.cookie(BaseData.menus) ? JSON.parse($.cookie(BaseData.menus)) : {};
			menuId = memus[BaseData.path],
			$menu_li = $(".menu-bar[menu-id=" + menuId + "]");
		if(menuId && $menu_li.length > 0) {
			$menu_li.closest("dd").prev("dt").click();
			$menu_li.find("a").addClass("selected");
		}
	})();
		
	window.initPage = function() {
		
		//清空form表单的数据,如果想不清空指定的对象，则加上class="not-reset"
		$(".reset-btn").off(".init-page").on("click.init-page", function() {
			gb.resetForm($(this).closest("form"));
			return false;
		});
		
		//修改密码
		$("#updPwdBtn").click(function() {
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
		
	};
	
	//渲染页面
	renderPage();
	//初始化页面
	initPage();
	
});

;(function($){
	
	/*******************************  配置ajax  **************************************/
	$.ajaxSetup({
    	traditional: true, //Jquery ajax请求时，用传统方式组装参数。设置此值后，参数不能传嵌套数组
    });
	
	/*******************************  配置layer  **************************************/
	layer.config({
		maxWidth: 'auto' //默认的maxWidth为360，固改成auto
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
	Vue.filter('date', function(value, format) {
		var date;
		if(value != null) {
			if(utils.isNumber(value)) {
				date = new Date(value);
			}else if(value instanceof Date) {
				date = value;
			}else if(utils.isString(value)) {
				if(/^\d+$/.test(value)) {
					date = new Date(parseInt(value));
				}else {
					var num = Date.parse(value);
					date = isNaN(num) ? null : new Date(num);
				}
			}
		}
		return date && date.format(format);
	});
	//格式化图片
	Vue.filter('img', function(value, rule, mode) {
		if(value && rule) {
			value = utils.imgUrlFormat(value, rule, mode);
			return value ? BaseData.imgUrl + "/" + value : value;
		}
		return value;
	});
	
	/****************************** 开启/关闭功能 *************************************/
	var default_setting = {
			batchClass: "batch",//批处理标识，含有class="batch" 表明此操作是批处理
			targetClass: null,	//操作成功后修改的目标对象,null为当前触发事件的对象。批量操作必须传值
			idsKey: "ids",		//主键参数的key值
			url: null,			//如果change里面包含url参数则首选，否则用此url参数
			change: {
				open: {
					confirmMsg	: "确定启用？",
			 		sucMsg		: null,
			 		failMsg		: "启用失败!",
			 		sucText		: null,
			 		targetText	: null,
			 		targetClass	: "yes",
			 		url			: null,
			 		type		: "POST",
			 		cache 		: false,
			 		data		: {status: "OPEN"}
				},
				close: {
					confirmMsg	: "确定禁用？",
			 		sucMsg		: null,
			 		failMsg		: "禁用失败!",
			 		sucText		: null,
			 		targetText	: null,
			 		targetClass	: "no",
			 		url			: null,
			 		type		: "POST",
			 		cache 		: false,
			 		data		: {status: "CLOSE"}
				}
			}
	};
	$.fn.extend({
		//更换对象处理逻辑
		changeHandler : function(settings) {
			settings = $.extend(true, {}, default_setting, settings);
			var self = this,
				obverseClass,
				reverseClass;
			for(var cls in settings.change) {
				if(!obverseClass) {
					obverseClass = cls;
				}else if(!reverseClass) {
					reverseClass = cls;
				}else
					break;
			}
			
			function change(addClass, removeClass) {
				var $addObj = settings.change[addClass],
					$rmObj = settings.change[removeClass];
				if($(this).hasClass(settings.batchClass)
						&& settings.targetClass) {
					var parents = $("input[name=ids]:checked").closest("[trid]"),
						$targets = parents.find("." + settings.targetClass);
						
					if($rmObj.targetText)
						$targets.text($rmObj.targetText);
					if($addObj.targetClass && $rmObj.targetClass) {
						$targets.removeClass($addObj.targetClass).addClass($rmObj.targetClass);
					}
					$targets.removeClass(removeClass).addClass(addClass);
					if($addObj.sucText)
						$targets.html($addObj.sucText);
				}else {
					if($addObj.sucText)
						$(this).html(s.sucText);
					$(this).removeClass(removeClass).addClass(addClass);
					
					var $target = !settings.targetClass ? $(this)
							: $(this).closest("[trid]").find("." + settings.targetClass);
					if($rmObj.targetText)
						$target.text($rmObj.targetText);
					if($addObj.targetClass && $rmObj.targetClass) {
						$target.removeClass($addObj.targetClass).addClass($rmObj.targetClass);
					}
				}
			}
			
			function bindData(s) {
				s.data = s.data || {};
				if(!settings.idsKey) 
					return;
				var vals = [];
				if($(this).hasClass(settings.batchClass)) {
					$("input[name=ids]:checked").each(function(){
						vals.push($(this).val());
					});
				}else {
					var val = $(this).closest("[trid]").attr("trid");
					if(val)
						vals.push(val);
				}
				s.data[settings.idsKey] = vals;
			}
			self.off(".changeHandler").on("click.changeHandler", function(){
				var _this = this;
				if($(_this).hasClass(settings.batchClass)
						&& $("input[name=ids]:checked").length == 0) {
					$.msg("warn", "请先选择您要操作的对象！");
					return;
				}
				if($(_this).hasClass(obverseClass)){
					var s = $.extend({}, settings.change[obverseClass] || {});
					s.url = s.url || settings.url;
					var successTmp = s.success;
					s.success = function(data, textStatus, jqXHR){
						if(!data.code || data.code == BaseData.suc){
							change.call(_this, reverseClass, obverseClass);
						}
						if(successTmp)
							successTmp.call(_this, data, textStatus, jqXHR);
					};
					bindData.call(_this, s);
					if(s.beforeAjax){
						var back = s.beforeAjax.call(_this, s);
						if(back === false)
							return;
					}
					if(s.confirmMsg){
						$.confirm(s.confirmMsg, function(){
							$.ajax(s);
						});
					}else{
						$.ajax(s);
					}
				}else if($(_this).hasClass(reverseClass)){
					var s = $.extend({}, settings.change[reverseClass] || {});
					s.url = s.url || settings.url;
					var successTmp = s.success;
					s.success = function(data, textStatus, jqXHR){
						if(!data.code || data.code == BaseData.suc){
							change.call(_this, obverseClass, reverseClass);
						}
						if(successTmp)
							successTmp.call(_this, data, textStatus, jqXHR);
					};
					bindData.call(_this, s);
					if(s.beforeAjax){
						var back = s.beforeAjax.call(_this, s);
						if(back === false)
							return;
					}
					if(s.confirmMsg){
						$.confirm(s.confirmMsg, function(){
							$.ajax(s);
						});
					}else{
						$.ajax(s);
					}
				}
				return false;
			});
			return this;
		}
	});
	
})(jQuery);
