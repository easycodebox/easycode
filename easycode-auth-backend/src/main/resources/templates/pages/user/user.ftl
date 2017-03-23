[#include "/common/core.ftl"/]

<title>EasyCode | 用户</title>

<section class="content-header">
	<h1>
		用户 <small>用户</small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="/"><i class="fa fa-dashboard"></i> Home</a></li>
		<li><a href="#">Tables</a></li>
		<li class="active">Simple</li>
	</ol>
</section>

<section class="content">
	<div class="row">
		<div class="col-xs-12">
			
			<!-- 查询区域 -->
			<div class="box">
				<div class="box-body">
				
					<form id="query-form" class="form-inline search words5">
						<div class="form-group">
							<label>用户组名：</label>
							<input type="text" class="form-control" name="groupName" />
						</div>
						
						<div class="form-group">
							<label>员工编号：</label>
							<input type="text" class="form-control" name="userNo" />
						</div>
						
						<div class="form-group">
							<label>用户名：</label>
							<input type="text" class="form-control" name="username" />
						</div>
						
						<div class="form-group">
							<label>昵称：</label>
							<input type="text" class="form-control" name="nickname" />
						</div>
						
						<div class="form-group">
							<label>真实姓名：</label>
							<input type="text" class="form-control" name="realname" />
						</div>
						
						<div class="form-group">
							<label>状态：</label>
							[@e.radioEnum labelClass="radio-inline" name="status" enumName="OpenClose" /]
						</div>
						
						<div class="form-group">
							<label>邮箱：</label>
							<input type="text" class="form-control" name="email" />
						</div>
						
						<div class="form-group">
							<label>手机号：</label>
							<input type="text" class="form-control" name="mobile" />
						</div>
						
						<div class="form-btns">
							<button type="button" class="toggle-icon hidden">展开/收起</button>
							<button type="submit" class="btn btn-default">查询</button>
	                       	<button type="reset" class="reset-btn btn btn-default">清空</button>
						</div>
					</form>
				
				</div>
			</div>
			
			<!-- 数据区域 -->
			<div class="box">
				<div class="box-body">
				
					<div id="toolbar" class="btn-group">
						[@shiro.hasPermission name="user/add"]
							<button type="button" title="新增" id="addBtn" class="btn btn-default">
                       			<i class="fa fa-plus"></i>
                       		</button>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="user/remove"]
							<button type="button" title="批量删除" class="btn btn-default" onclick="gb.remove('/user/remove.json')">
                       			<i class="fa fa-remove"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="user/openClose"]
                       		<button type="button" title="启用" class="handler switch-open batch btn btn-default">
                        		<i class="fa fa-toggle-on"></i>
                        	</button>
							<button type="button" title="禁用" class="handler switch-close batch btn btn-default">
								<i class="fa fa-toggle-off"></i>
							</button>
						[/@shiro.hasPermission]
					</div>
					<table id="data-table" 
							data-toolbar="#toolbar"
							data-query-form="query-form"
							data-query-params="gb.table.params"
							data-id-field="id"
							data-unique-id="id"
							data-select-item-name="ids"
							data-url="/user/list.json">
						<thead hidden>
							<tr>
								<th data-checkbox="true" data-click-to-select="true"></th>
								<th data-formatter="gb.table.fmtOrder" data-click-to-select="true">序号</th>
								<th data-field="groupName">用户组名</th>
								<th data-field="userNo">员工编号</th>
								<th data-field="username">用户名</th>
								<th data-field="nickname">昵称</th>
								<th data-field="realname">真实姓名</th>
								<th data-field="status" data-formatter="gb.table.fmtOpenClose">启动/禁用</th>
								<th data-field="sort">排序值</th>
								<th data-field="email" data-visible="false">邮箱</th>
								<th data-field="mobile" data-visible="false">手机号</th>
								<th data-field="loginFail">错误登录</th>
								<th data-field="creatorName">创建人</th>
								<th data-field="createTime" data-formatter="gb.table.fmtDate">创建时间</th>
								<th data-formatter="gb.table.fmtOps">操作</th>
							</tr>
						</thead>
					</table>
					<div id="table-ops" class="hidden">
						[@shiro.hasPermission name="user/load"]
							<a class="btn btn-xs btn-default loadBtn" data-id="{id}" title="详情" href="#" role="button">
								<i class="fa fa-reorder"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="user/update"]
							<a class="btn btn-xs btn-default updBtn" data-id="{id}" title="修改" href="#" role="button">
								<i class="fa fa-edit"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="user/remove"]
							<a class="btn btn-xs btn-default" href="javascript:gb.remove('/user/remove.json', '{id}');" title="删除" role="button">
								<i class="fa fa-remove"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="role/cfgByUserId"]
							<a class="btn btn-xs btn-default cfgRolesBtn" data-id="{id}" title="配置角色" href="#" role="button">
								<i class="fa fa-cog"></i>
							</a>
						[/@shiro.hasPermission]
					</div>
					
				</div>
			</div>
			
		</div>
	</div>
</section>
	
<!-- 模板页 -->
<div id="tmpls">
	[#include "/user/user_load.ftl"/]
	
	<!-- 新增 -->
	<form id="addDialog" class="form-validate none" action="/user/add.json" data-suc="新增用户成功" data-fail="新增用户失败">
		<table class="table-edit">
			<tr>
				<th>
					<label>用户组：</label>
				</th>
				<td>
					<div class="tree-tip-wrap">
						<input type="text" class="form-control groupName" v-model="user.groupName" readonly />
						<input type="hidden" class="groupId" name="groupId" v-model="user.groupId" readonly />
						<div class="tree-tip-div">
							<ul class="ztree tree-tip"></ul>
						</div>
					</div>
				</td>
				<th>
					<label>员工编号：</label>
				</th>
				<td>
					<input type="text" name="userNo" class="form-control" v-model="user.userNo" maxlength="32" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">用户名：</label>
				</th>
				<td>
					<input type="text" name="username" class="form-control" v-model="user.username" data-bvalidator="word,gb.existUsername" maxlength="32" required />
				</td>
				<th>
					<label>昵称：</label>
				</th>
				<td>
					<input type="text" name="nickname" class="form-control" v-model="user.nickname" data-bvalidator="word_zh,gb.existNickname" maxlength="32" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">密码：</label>
				</th>
				<td>
					<input type="password" id="pwdAdd" name="password" class="form-control" v-model="user.password" maxlength="32" required />
				</td>
				<th>
					<label class="required">确认密码：</label>
				</th>
				<td>
					<input type="password" class="form-control" v-model="user.password2" data-bvalidator="equal[pwdAdd]" maxlength="32" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>真实姓名：</label>
				</th>
				<td>
					<input type="text" name="realname" class="form-control" v-model="user.realname" data-bvalidator="word_zh" maxlength="32" />
				</td>
				<th>
					<label class="required">状态：</label>
				</th>
				<td>
					[@e.radioEnum name="status" enumName="OpenClose" 
							tagAttr='{
								last: "required data-bvalidator-msg=\'请选择状态\'",
								all: "v-model=\'user.status.className\'"
							}'/]
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="user.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
				<th>
					<label>手机号：</label>
				</th>
				<td>
					<input type="text" name="mobile" class="form-control" v-model="user.mobile" data-bvalidator="mobile" maxlength="11" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>性别：</label>
				</th>
				<td>
					[@e.radioEnum name="gender" enumName="Gender" tagAttr="v-model='user.gender.className'" /]
				</td>
				<th>
					<label>邮箱：</label>
				</th>
				<td>
					<input type="email" name="email" class="form-control" v-model="user.email" maxlength="512" />
				</td>
			</tr>
		</table>
	</form>
	
	<!-- 修改 -->
	<form id="updDialog" class="form-validate none" action="/user/update.json" data-suc="更新用户成功" data-fail="更新用户失败">
	
		<input type="hidden" name="id" v-model="user.id" />
		
		<table class="table-edit">
			<tr>
				<th>
					<label>用户组：</label>
				</th>
				<td>
					<div class="tree-tip-wrap">
						<input type="text" class="form-control groupName" v-model="user.groupName" readonly />
						<input type="hidden" class="groupId" name="groupId" v-model="user.groupId" readonly />
						<div class="tree-tip-div">
							<ul class="ztree tree-tip"></ul>
						</div>
					</div>
				</td>
				<th>
					<label>员工编号：</label>
				</th>
				<td>
					<input type="text" name="userNo" class="form-control" v-model="user.userNo" maxlength="32" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">用户名：</label>
				</th>
				<td>
					<input type="text" name="username" class="form-control" v-model="user.username" data-bvalidator="word,gb.existUsername" maxlength="32" required />
				</td>
				<th>
					<label>昵称：</label>
				</th>
				<td>
					<input type="text" name="nickname" class="form-control" v-model="user.nickname" data-bvalidator="word_zh,gb.existNickname" maxlength="32" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>真实姓名：</label>
				</th>
				<td>
					<input type="text" name="realname" class="form-control" v-model="user.realname" data-bvalidator="word_zh" maxlength="32" />
				</td>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="user.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>手机号：</label>
				</th>
				<td>
					<input type="text" name="mobile" class="form-control" v-model="user.mobile" data-bvalidator="mobile" maxlength="11" />
				</td>
				<th>
					<label>邮箱：</label>
				</th>
				<td>
					<input type="email" name="email" class="form-control" v-model="user.email" maxlength="512" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>性别：</label>
				</th>
				<td>
					[@e.radioEnum name="gender" enumName="Gender" tagAttr="v-model='user.gender.className'" /]
				</td>
			</tr>
		</table>
	</form>
</div>

<inner-js>
	<script type="text/javascript" transient="true" src="/js/user/user.js"></script>
</inner-js>
