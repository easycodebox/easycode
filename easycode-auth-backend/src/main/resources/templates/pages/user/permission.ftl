[#include "/common/core.ftl"/]
[@e.link href="http://cdn.easycodebox.com/jquery-file-upload/9.12.3/css/??jquery.fileupload.css" /]

<title>EasyCode | 权限</title>

<section class="content-header">
	<h1>
		权限 <small>权限</small>
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
							<label>上级：</label>
							<input type="text" class="form-control" name="parentName" />
						</div>
						
						<div class="form-group">
							<label>项目名：</label>
							<input type="text" class="form-control" name="projectName" />
						</div>
						
						<div class="form-group">
							<label>权限名：</label>
							<input type="text" class="form-control" name="name" />
						</div>
						
						<div class="form-group">
							<label>状态：</label>
							[@e.radioEnum labelClass="radio-inline" name="status" enumName="OpenClose" /]
						</div>
						
						<div class="form-group">
							<label>菜单：</label>
							[@e.radioEnum labelClass="radio-inline" name="isMenu" enumName="YesNo" /]
						</div>
						
						<div class="form-group">
							<label>地址：</label>
							<input type="text" class="form-control" name="url" />
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
						[@shiro.hasPermission name="permission/add"]
                       		<button type="button" title="新增" id="addBtn" class="btn btn-default">
                       			<i class="fa fa-plus"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="permission/remove"]
                       		<button type="button" title="批量删除" class="btn btn-default" onclick="gb.remove('/permission/remove.json')">
                       			<i class="fa fa-remove"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="permission/openClose"]
                       		<button type="button" title="启用" class="handler switch-open batch btn btn-default">
                        		<i class="fa fa-toggle-on"></i>
                        	</button>
							<button type="button" title="禁用" class="handler switch-close batch btn btn-default">
								<i class="fa fa-toggle-off"></i>
							</button>
						[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="permission/imports"]
							<span title="导入" class="btn btn-default fileinput-button">
								<i class="fa fa-upload"></i>
								<input id="importsOper" name="files[]" multiple="multiple" type="file">
							</span>
						[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="permission/exports"]
							<button type="button" title="导出" class="btn btn-default" id="exportsOper">
								<i class="fa fa-download"></i>
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
							data-url="/permission/list.json">
						<thead hidden>
							<tr>
								<th data-checkbox="true" data-click-to-select="true"></th>
								<th data-formatter="gb.table.fmtOrder" data-click-to-select="true">序号</th>
								<th data-field="parentName">上级权限</th>
								<th data-field="projectName">项目名</th>
								<th data-field="name">权限名</th>
								<th data-field="status" data-formatter="gb.table.fmtOpenClose">启动/禁用</th>
								<th data-field="isMenu" data-formatter="gb.table.fmtIsMenu">菜单</th>
								<th data-field="icon">图标</th>
								<th data-field="url">地址</th>
								<th data-field="sort">排序值</th>
								<th data-field="creatorName">创建人</th>
								<th data-field="createTime" data-formatter="gb.table.fmtDate">创建时间</th>
								<th data-formatter="gb.table.fmtOps">操作</th>
							</tr>
						</thead>
					</table>
					<div id="table-ops" class="hidden">
						[@shiro.hasPermission name="permission/load"]
							<a class="btn btn-xs btn-default loadBtn" data-id="{id}" title="详情" href="#" role="button">
								<i class="fa fa-reorder"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="permission/update"]
							<a class="btn btn-xs btn-default updBtn" data-id="{id}" title="修改" href="#" role="button">
								<i class="fa fa-edit"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="permission/remove"]
							<a class="btn btn-xs btn-default" href="javascript:gb.remove('/permission/remove.json', '{id}');" title="删除" role="button">
								<i class="fa fa-remove"></i>
							</a>
						[/@shiro.hasPermission]
					</div>
					
				</div>
			</div>
			
		</div>
	</div>
</section>
	
<!-- 导出指定项目的权限 -->
<div id="exportDiv" class="none" style="padding: 10px;">
	<select id="exportPros" class="form-control" style="width: 400px;"></select>
</div>

<!-- 模板页 -->
<div id="tmpls">
	[#include "/pages/user/permission_load.ftl"/]
	
	<!-- 新增 -->
	<form id="addDialog" class="form-validate none" action="/permission/add.json" data-suc="新增权限成功" data-fail="新增权限失败">
		<table class="table-edit">
			
			<tr>
				<th>
					<label class="required">项目：</label>
				</th>
				<td>
					<select class="form-control projectId" name="projectId" v-model="permission.projectId" required data-bvalidator-msg="请选择项目类型">
						<option value="">--请选择项目--</option>
						<option v-for="pro in projects" :value="pro.id">{{pro.name}}</option>
					</select>
				</td>
				<th>
					<label>上级权限：</label>
				</th>
				<td>
					<div class="tree-tip-wrap">
						<input type="text" class="form-control parentName" v-model="permission.parentName" data-bvalidator="gb.permissionValidator" readonly />
						<input type="hidden" class="parentId" name="parentId" v-model="permission.parentId" readonly />
						<div class="tree-tip-div">
							<ul class="ztree tree-tip"></ul>
						</div>
					</div>
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">权限名：</label>
				</th>
				<td>
					<input type="text" name="name" class="form-control" v-model="permission.name" required maxlength="128" />
				</td>
				<th>
					<label class="required">状态：</label>
				</th>
				<td>
					[@e.radioEnum name="status" enumName="OpenClose" 
							tagAttr='{
								last: "required data-bvalidator-msg=\'请选择状态\'",
								all: "v-model=\'permission.status.className\'"
							}'/]
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">菜单：</label>
				</th>
				<td>
					[@e.radioEnum name="isMenu" enumName="YesNo"
							tagAttr='{
								last: "required data-bvalidator-msg=\'请选择是否为菜单\'",
								all: "v-model=\'permission.isMenu.className\'"
							}'/]
				</td>
				<th>
					<label>地址：</label>
				</th>
				<td>
					<input type="text" name="url" class="form-control" v-model="permission.url" data-bvalidator="gb.existUrl" maxlength="128" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="permission.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
				<th>
					<label>图标 ：</label>
				</th>
				<td>
					<input type="text" name="icon" class="form-control" v-model="permission.icon" maxlength="128" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>描述：</label>
				</th>
				<td colspan="3">
					<textarea name="description" class="form-control" v-model="permission.description" maxlength="512"> </textarea>
				</td>
			</tr>
			
			<tr>
				<th>
					<label>备注：</label>
				</th>
				<td colspan="3">
					<textarea name="remark" class="form-control" v-model="permission.remark" maxlength="512"> </textarea>
				</td>
			</tr>
		</table>
	</form>
	
	<!-- 修改 -->
	<form id="updDialog" class="form-validate none" action="/permission/update.json" data-suc="更新权限成功" data-fail="更新权限失败">
	
		<input type="hidden" name="id" v-model="permission.id" />
		
		<table class="table-edit">
			<tr>
				<th>
					<label class="required">项目：</label>
				</th>
				<td>
					<select class="form-control projectId" name="projectId" v-model="permission.projectId" required data-bvalidator-msg="请选择项目类型">
						<option value="">--请选择项目--</option>
						<option v-for="pro in projects" :value="pro.id">{{pro.name}}</option>
					</select>
				</td>
				<th>
					<label>上级权限：</label>
				</th>
				<td>
					<div class="tree-tip-wrap">
						<input type="text" class="form-control parentName" v-model="permission.parentName" data-bvalidator="gb.permissionValidator" readonly />
						<input type="hidden" class="parentId" name="parentId" v-model="permission.parentId" readonly />
						<div class="tree-tip-div">
							<ul class="ztree tree-tip"></ul>
						</div>
					</div>
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">权限名：</label>
				</th>
				<td>
					<input type="text" name="name" class="form-control" v-model="permission.name" maxlength="128" required />
				</td>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="permission.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">菜单：</label>
				</th>
				<td>
					[@e.radioEnum name="isMenu" enumName="YesNo" 
							tagAttr='{
								last: "required data-bvalidator-msg=\'请选择是否为菜单\'",
								all: "v-model=\'permission.isMenu.className\'"
							}'/]
				</td>
				<th>
					<label>地址：</label>
				</th>
				<td>
					<input type="text" name="url" class="form-control" v-model="permission.url" data-bvalidator="gb.existUrl" maxlength="128" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>图标 ：</label>
				</th>
				<td>
					<input type="text" name="icon" class="form-control" v-model="permission.icon" maxlength="128" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>描述：</label>
				</th>
				<td colspan="3">
					<textarea name="description" class="form-control" v-model="permission.description" maxlength="512"></textarea>
				</td>
			</tr>
			
			<tr>
				<th>
					<label>备注：</label>
				</th>
				<td colspan="3">
					<textarea name="remark" class="form-control" v-model="permission.remark" maxlength="512"></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>

<inner-js>
	[@e.script env="prod" src="http://cdn.easycodebox.com/jquery-file-upload/9.12.3/js/??
		vendor/jquery.ui.widget.js,
		jquery.iframe-transport.js,
		jquery.fileupload.js,
		jquery.fileupload-process.js,
		jquery.fileupload-validate.js" /]
	<script type="text/javascript" transient="true" src="/js/user/permission.js"></script>
</inner-js>
