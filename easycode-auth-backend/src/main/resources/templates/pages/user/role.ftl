[#include "/common/core.ftl"/]

<title>EasyCode | 角色</title>

<section class="content-header">
	<h1>
		角色 <small>角色</small>
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
				
					<form id="query-form" class="form-inline search words4">
						<div class="form-group">
							<label>角色名：</label>
							<input type="text" class="form-control" name="name" />
						</div>
						
						<div class="form-group">
							<label>状态：</label>
							[@e.radioEnum labelClass="radio-inline" name="status" enumName="OpenClose" /]
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
						[@shiro.hasPermission name="role/add"]
                       		<button type="button" title="新增" id="addBtn" class="btn btn-default">
                       			<i class="fa fa-plus"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="role/remove"]
                       		<button type="button" title="批量删除" class="btn btn-default" onclick="gb.remove('/role/remove.json')">
                       			<i class="fa fa-remove"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="role/openClose"]
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
							data-url="/role/list.json">
						<thead hidden>
							<tr>
								<th data-checkbox="true" data-click-to-select="true"></th>
								<th data-formatter="gb.table.fmtOrder" data-click-to-select="true">序号</th>
								<th data-field="name">角色名</th>
								<th data-field="sort">排序值</th>
								<th data-field="status" data-formatter="gb.table.fmtOpenClose">启动/禁用</th>
								<th data-field="description">描述</th>
								<th data-field="creatorName">创建人</th>
								<th data-field="createTime" data-formatter="gb.table.fmtDate">创建时间</th>
								<th data-formatter="gb.table.fmtOps">操作</th>
							</tr>
						</thead>
					</table>
					<div id="table-ops" class="hidden">
						[@shiro.hasPermission name="role/load"]
							<a class="btn btn-xs btn-default loadBtn" data-id="{id}" title="详情" href="#" role="button">
								<i class="fa fa-reorder"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="role/update"]
							<a class="btn btn-xs btn-default updBtn" data-id="{id}" title="修改" href="#" role="button">
								<i class="fa fa-edit"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="role/remove"]
							<a class="btn btn-xs btn-default" href="javascript:gb.remove('/role/remove.json', '{id}');" title="删除" role="button">
								<i class="fa fa-remove"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="permission/authoriseRole"]
							<a class="btn btn-xs btn-default cfgOpsBtn" data-id="{id}" title="配置权限" href="#" role="button">
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
	[#include "/user/role_load.ftl"/]
	
	<!-- 新增 -->
	<form id="addDialog" class="form-validate none" action="/role/add.json" data-suc="新增角色成功" data-fail="新增角色失败">
		<table class="table-edit">
			<tr>
				<th>
					<label class="required">角色名：</label>
				</th>
				<td>
					<input type="text" name="name" class="form-control" v-model="role.name" data-bvalidator="gb.existName" maxlength="32" required />
				</td>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="role.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">状态：</label>
				</th>
				<td>
					[@e.radioEnum name="status" enumName="OpenClose" 
							tagAttr='{
								last: "required data-bvalidator-msg=\'请选择状态\'",
								all: "v-model=\'role.status.className\'"
							}'/]
				</td>
			</tr>
			
			<tr>
				<th>
					<label>描述：</label>
				</th>
				<td colspan="3">
					<textarea name="description" class="form-control" v-model="role.description" maxlength="512"> </textarea>
				</td>
			</tr>
			
			<tr>
				<th>
					<label>备注：</label>
				</th>
				<td colspan="3">
					<textarea name="remark" class="form-control" v-model="role.remark" maxlength="512"> </textarea>
				</td>
			</tr>
		</table>
	</form>
	
	<!-- 修改 -->
	<form id="updDialog" class="form-validate none" action="/role/update.json" data-suc="更新角色成功" data-fail="更新角色失败">
	
		<input type="hidden" name="id" v-model="role.id" />
		
		<table class="table-edit">
			<tr>
				<th>
					<label class="required">角色名：</label>
				</th>
				<td>
					<input type="text" name="name" class="form-control" v-model="role.name" data-bvalidator="gb.existName" maxlength="32" required />
				</td>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="role.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>描述：</label>
				</th>
				<td colspan="3">
					<textarea name="description" class="form-control" v-model="role.description" maxlength="512"></textarea>
				</td>
			</tr>
			
			<tr>
				<th>
					<label>备注：</label>
				</th>
				<td colspan="3">
					<textarea name="remark" class="form-control" v-model="role.remark" maxlength="512"></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>

<inner-js>
	<script type="text/javascript" transient="true" src="/js/user/role.js"></script>
</inner-js>
