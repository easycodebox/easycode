<!-- 右侧控制面板  -->
<aside class="control-sidebar control-sidebar-dark">
	<ul class="nav nav-tabs nav-justified control-sidebar-tabs">
		<li class="active">
			<a href="#control-sidebar-theme-demo-options-tab" data-toggle="tab">
				<i class="fa fa-wrench"></i>
			</a>
		</li>
		<li>
			<a href="#control-sidebar-home-tab" data-toggle="tab">
				<i class="fa fa-home"></i>
			</a>
		</li>
		<li>
			<a href="#control-sidebar-settings-tab" data-toggle="tab">
				<i class="fa fa-gears"></i>
			</a>
		</li>
	</ul>
	<div class="tab-content">
		<div id="control-sidebar-theme-demo-options-tab" class="tab-pane active">
			<div>
				<h4 class='control-sidebar-heading'>布局配置</h4>
				<div class='form-group'>
					<label class='control-sidebar-subheading'>
						<input type='checkbox' data-layout='fixed' class='changeLayout pull-right'/> 固定布局
						<span data-toggle="popover" data-trigger="hover" data-placement="bottom"
								title="固定布局" data-content="激活固定布局模式，不能固定布局和盒子布局一起使用">
							<i class="fa fa-question-circle"></i>
						</span>
					</label>
				</div>
				<div class='form-group'>
					<label class='control-sidebar-subheading'>
						<input type='checkbox' data-layout='layout-boxed' class='changeLayout pull-right'/> 盒子布局
						<span data-toggle="popover" data-trigger="hover" data-placement="bottom"
								title="盒子布局" data-content="激活盒子布局模式">
							<i class="fa fa-question-circle"></i>
						</span>
					</label>
				</div>
				<div class='form-group'>
					<label class='control-sidebar-subheading'>
						<input type='checkbox' data-layout='sidebar-collapse' class='changeLayout pull-right'/> 展开/收起 菜单
						<span data-toggle="popover" data-trigger="hover" data-placement="bottom"
								title="展开/收起 菜单" data-content="展开/收起 左侧菜单">
							<i class="fa fa-question-circle"></i>
						</span>
					</label>
				</div>
				<div class='form-group'>
					<label class='control-sidebar-subheading'>
						<input type='checkbox' data-enable='expandOnHover' class='pull-right'/> 菜单悬浮展开
						<span data-toggle="popover" data-trigger="hover" data-placement="bottom"
								title="菜单悬浮展开" data-content="鼠标悬浮在收起的菜单上时展开菜单项">
							<i class="fa fa-question-circle"></i>
						</span>
					</label>
				</div>
				<div class='form-group'>
					<label class='control-sidebar-subheading'>
						<input type='checkbox' data-controlsidebar='control-sidebar-open' class='pull-right'/> 展开/收起 面板菜单
						<span data-toggle="popover" data-trigger="hover" data-placement="bottom"
								title="展开/收起 面板菜单" data-content="展开/收起 右侧面板菜单，激活时会对页面内容宽度产生影响">
							<i class="fa fa-question-circle"></i>
						</span>
					</label>
				</div>
				<div class='form-group'>
					<label class='control-sidebar-subheading'>
						<input type='checkbox' data-sidebarskin='toggle' class='pull-right'/> 切换面板菜单皮肤
						<span data-toggle="popover" data-trigger="hover" data-placement="bottom"
								title="切换面板菜单皮肤" data-content="切换面板菜单的 白/黑 皮肤">
							<i class="fa fa-question-circle"></i>
						</span>
					</label>
				</div>
				
				<h4 class='control-sidebar-heading'>皮肤</h4>
				<ul class="list-unstyled clearfix">
					[#assign skins = [
						['skin-blue', '蓝-黑', '#367fa9', '#3c8dbc', '#222d32', '#f4f5f7'],
						['skin-black', '白-黑', '#fefefe', '#fefefe', '#222', '#f4f5f7'],
						['skin-purple', '紫-黑', '#555299', '#605ca8', '#222d32', '#f4f5f7'],
						['skin-green', '绿-黑', '#008d4c', '#00a65a', '#222d32', '#f4f5f7'],
						['skin-red', '红-黑', '#d33724', '#dd4b39', '#222d32', '#f4f5f7'],
						['skin-yellow', '黄-黑', '#db8b0b', '#f39c12', '#222d32', '#f4f5f7'],
						['skin-blue-light', '蓝-白', '#367fa9	', '#3c8dbc', '#f9fafc', '#f4f5f7'],
						['skin-black-light', '白-白', '#fefefe', '#fefefe', '#f9fafc', '#f4f5f7'],
						['skin-purple-light', '紫-白', '#555299', '#605ca8', '#f9fafc', '#f4f5f7'],
						['skin-green-light', '绿-白', '#008d4c', '#00a65a', '#f9fafc', '#f4f5f7'],
						['skin-red-light', '红-白', '#d33724', '#dd4b39', '#f9fafc', '#f4f5f7'],
						['skin-yellow-light', '黄-白', '#db8b0b', '#f39c12', '#f9fafc', '#f4f5f7']
					]]
					[#list skins as skin]
						<li style="float:left; width: 33.33333%; padding: 5px;">
							<a class="changeSkin" href='javascript:;' data-skin='${skin[0]}' style='display: block; box-shadow: 0 0 3px rgba(0,0,0,0.4)' class='clearfix full-opacity-hover'>
								<span style='display:block; width: 20%; float: left; height: 7px; background: ${skin[2]};'></span>
								<span style='display:block; width: 80%; float: left; height: 7px; background: ${skin[3]};'></span>
								<span style='display:block; width: 20%; float: left; height: 20px; background: ${skin[4]};'></span>
								<span style='display:block; width: 80%; float: left; height: 20px; background: ${skin[5]};'></span>
							</a>
							<p class='text-center no-margin'>${skin[1]}</p>
						</li>
					[/#list]
				</ul>
			</div>
		</div>
		<!-- Home tab content -->
		<div class="tab-pane" id="control-sidebar-home-tab">
			<h3 class="control-sidebar-heading">Recent Activity</h3>
			<ul class="control-sidebar-menu">
				<li>
					<a href="javascript:void(0)"> 
						<i class="menu-icon fa fa-birthday-cake bg-red"></i>
						<div class="menu-info">
							<h4 class="control-sidebar-subheading">Langdon's Birthday</h4>

							<p>Will be 23 on April 24th</p>
						</div>
					</a>
				</li>
				<li>
					<a href="javascript:void(0)"> 
						<i class="menu-icon fa fa-user bg-yellow"></i>

						<div class="menu-info">
							<h4 class="control-sidebar-subheading">Frodo Updated His
								Profile</h4>

							<p>New phone +1(800)555-1234</p>
						</div>
					</a>
				</li>
				<li>
					<a href="javascript:void(0)"> 
						<i class="menu-icon fa fa-envelope-o bg-light-blue"></i>

						<div class="menu-info">
							<h4 class="control-sidebar-subheading">Nora Joined Mailing
								List</h4>

							<p>nora@example.com</p>
						</div>
					</a>
				</li>
				<li>
					<a href="javascript:void(0)"> 
						<i class="menu-icon fa fa-file-code-o bg-green"></i>

						<div class="menu-info">
							<h4 class="control-sidebar-subheading">Cron Job 254 Executed</h4>

							<p>Execution time 5 seconds</p>
						</div>
					</a>
				</li>
			</ul>
			<!-- /.control-sidebar-menu -->

			<h3 class="control-sidebar-heading">Tasks Progress</h3>
			<ul class="control-sidebar-menu">
				<li>
					<a href="javascript:void(0)">
						<h4 class="control-sidebar-subheading">
							Custom Template Design <span class="label label-danger pull-right">70%</span>
						</h4>

						<div class="progress progress-xxs">
							<div class="progress-bar progress-bar-danger" style="width: 70%"></div>
						</div>
					</a>
				</li>
				<li>
					<a href="javascript:void(0)">
						<h4 class="control-sidebar-subheading">
							Update Resume <span class="label label-success pull-right">95%</span>
						</h4>

						<div class="progress progress-xxs">
							<div class="progress-bar progress-bar-success" style="width: 95%"></div>
						</div>
					</a>
				</li>
				<li>
					<a href="javascript:void(0)">
						<h4 class="control-sidebar-subheading">
							Laravel Integration <span class="label label-warning pull-right">50%</span>
						</h4>

						<div class="progress progress-xxs">
							<div class="progress-bar progress-bar-warning" style="width: 50%"></div>
						</div>
					</a>
				</li>
				<li>
					<a href="javascript:void(0)">
						<h4 class="control-sidebar-subheading">
							Back End Framework <span class="label label-primary pull-right">68%</span>
						</h4>

						<div class="progress progress-xxs">
							<div class="progress-bar progress-bar-primary" style="width: 68%"></div>
						</div>
					</a>
				</li>
			</ul>

		</div>
		<!-- Stats tab content -->
		<div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab Content</div>
		<!-- Settings tab content -->
		<div class="tab-pane" id="control-sidebar-settings-tab">
			<form method="post">
				<h3 class="control-sidebar-heading">General Settings</h3>

				<div class="form-group">
					<label class="control-sidebar-subheading"> Report panel
						usage <input type="checkbox" class="pull-right" checked>
					</label>

					<p>Some information about this general settings option</p>
				</div>

				<div class="form-group">
					<label class="control-sidebar-subheading"> Allow mail
						redirect <input type="checkbox" class="pull-right" checked>
					</label>

					<p>Other sets of options are available</p>
				</div>

				<div class="form-group">
					<label class="control-sidebar-subheading"> Expose author
						name in posts <input type="checkbox" class="pull-right" checked>
					</label>

					<p>Allow the user to show his name in blog posts</p>
				</div>

				<h3 class="control-sidebar-heading">Chat Settings</h3>

				<div class="form-group">
					<label class="control-sidebar-subheading"> Show me as
						online <input type="checkbox" class="pull-right" checked>
					</label>
				</div>

				<div class="form-group">
					<label class="control-sidebar-subheading"> Turn off
						notifications <input type="checkbox" class="pull-right">
					</label>
				</div>

				<div class="form-group">
					<label class="control-sidebar-subheading"> 
						Delete chat history 
						<a href="javascript:void(0)" class="text-red pull-right"><i class="fa fa-trash-o"></i></a>
					</label>
				</div>
			</form>
		</div>
	</div>
</aside>
<!-- /.control-sidebar -->
<!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
<div class="control-sidebar-bg"></div>