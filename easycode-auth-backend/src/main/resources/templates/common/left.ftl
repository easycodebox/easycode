[#macro menus children]
	[#if children?? && children?size gt 0]
		[#list children as child]
			[#local hasChildren = child.children?? && child.children?size gt 0]
			<li class="treeview" data-menu-id="${child.id}">
				<a data-pjax href="${child.url???string(child.url, '#')}">
					<i class="${child.icon!'fa fa-circle-o'}"></i>
					<span>${child.name}</span>
					<!-- 显示右侧小标记  -->
					[#if hasChildren]
						<span class="pull-right-container">
							<i class="fa fa-angle-left pull-right"></i>
						</span>
					[/#if]
				</a>
				[#if hasChildren]
					<ul class="treeview-menu">
						[@menus children = child.children /]
					</ul>
				[/#if]
			</li>
		[/#list]
	[/#if]
[/#macro]
<aside class="main-sidebar">
	<section class="sidebar">
		<!-- 用户面板 -->
		<div class="user-panel">
			<div class="pull-left image">
				<img src="/imgs/frame/avatar.png" class="img-circle">
			</div>
			<div class="pull-left info">
				<p>${user_info.nickname}</p>
				<a href="#"><i class="fa fa-circle text-success"></i> Online</a>
			</div>
		</div>
		<!-- 搜索表单 -->
		<form action="#" method="get" class="sidebar-form">
			<div class="input-group">
				<input type="text" name="q" class="form-control" placeholder="Search..."> 
				<span class="input-group-btn">
					<button type="submit" name="search" id="search-btn" class="btn btn-flat">
						<i class="fa fa-search"></i>
					</button>
				</span>
			</div>
		</form>
		<!-- 菜单项 -->
		<ul class="sidebar-menu">
			[@menus children = .vars[project + "project_menu"] /]
		</ul>
	</section>
</aside>
