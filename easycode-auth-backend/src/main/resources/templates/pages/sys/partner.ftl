[#include "/common/core.ftl"/]

<title>EasyCode | 合作商</title>

<section class="content-header">
	<h1>
		合作商 <small>合作商</small>
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
							<label>合作商名：</label>
							<input type="text" class="form-control" name="name" />
						</div>
						
						<div class="form-group">
							<label>密钥：</label>
							<input type="text" class="form-control" name="partnerKey" />
						</div>
						
						<div class="form-group">
							<label>网址：</label>
							<input type="text" class="form-control" name="website" />
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
						[@shiro.hasPermission name="partner/add"]
							<button type="button" title="新增" id="addBtn" class="btn btn-default">
                       			<i class="fa fa-plus"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="partner/remove"]
                       		<button type="button" title="批量删除" class="btn btn-default" onclick="gb.remove('/partner/remove.json')">
                       			<i class="fa fa-remove"></i>
                       		</button>
                       	[/@shiro.hasPermission]
                       	[@shiro.hasPermission name="partner/openClose"]
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
							data-url="/partner/list.json">
						<thead hidden>
							<tr>
								<th data-checkbox="true" data-click-to-select="true"></th>
								<th data-formatter="gb.table.fmtOrder" data-click-to-select="true">序号</th>
								<th data-field="name">合作商名</th>
								<th data-field="partnerKey">密钥</th>
								<th data-field="website">网址</th>
								<th data-field="status" data-formatter="gb.table.fmtOpenClose">启动/禁用</th>
								<th data-field="sort">排序值</th>
								<th data-field="contract" data-formatter="gb.table.fmtPic">合同</th>
								<th data-field="creatorName">创建人</th>
								<th data-field="createTime" data-formatter="gb.table.fmtDate">创建时间</th>
								<th data-formatter="gb.table.fmtOps">操作</th>
							</tr>
						</thead>
					</table>
					<div id="table-ops" class="hidden">
						[@shiro.hasPermission name="partner/load"]
							<a class="btn btn-xs btn-default loadBtn" data-id="{id}" title="详情" href="#" role="button">
								<i class="fa fa-reorder"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="partner/update"]
							<a class="btn btn-xs btn-default updBtn" data-id="{id}" title="修改" href="#" role="button">
								<i class="fa fa-edit"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="partner/remove"]
							<a class="btn btn-xs btn-default" href="javascript:gb.remove('/partner/remove.json', '{id}');" title="删除" role="button">
								<i class="fa fa-remove"></i>
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
	[#include "/sys/partner_load.ftl"/]
	
	<!-- 新增 -->
	<form id="addDialog" class="form-validate none" action="/partner/add.json" data-suc="新增合作商成功" data-fail="新增合作商失败">
		<table class="table-edit">
			<tr>
				<th>
					<label class="required">合作商名：</label>
				</th>
				<td>
					<input type="text" name="name" class="form-control" v-model="partner.name" data-bvalidator="gb.existName" maxlength="128" required />
				</td>
				<th>
					<label>网址：</label>
				</th>
				<td>
					<input type="text" name="website" class="form-control" v-model="partner.website" maxlength="512" />
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
								all: "v-model=\'partner.status.className\'"
							}'/]
				</td>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="partner.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>合同：</label>
				</th>
				<td>
					<input type="hidden" id="contractAdd" name="contract" v-model="partner.contract" />
					<div id="uploadImgsAdd" class="uploadPicDiv">
						<div id="upImgAdd" class="uploadTitle"></div>
					</div>
				</td>
			</tr>
			
			<tr>
				<th>
					<label>备注：</label>
				</th>
				<td colspan="3">
					<textarea name="remark" class="form-control" v-model="partner.remark" maxlength="512"> </textarea>
				</td>
			</tr>
		</table>
	</form>
	
	<!-- 修改 -->
	<form id="updDialog" class="form-validate none" action="/partner/update.json" data-suc="更新合作商成功" data-fail="更新合作商失败">
	
		<input type="hidden" name="id" v-model="partner.id" />
		
		<table class="table-edit">
			<tr>
				<th>
					<label class="required">合作商名：</label>
				</th>
				<td>
					<input type="text" name="name" class="form-control" v-model="partner.name" data-bvalidator="gb.existName" maxlength="128" required />
				</td>
				<th>
					<label>网址：</label>
				</th>
				<td>
					<input type="text" name="website" class="form-control" v-model="partner.website" maxlength="512" />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">排序值：</label>
				</th>
				<td>
					<input type="text" name="sort" class="form-control" v-model="partner.sort" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>合同：</label>
				</th>
				<td>
					<input type="hidden" id="contractUpd" name="contract" v-model="partner.contract" />
					<div id="uploadImgsUpd" class="uploadPicDiv">
						<div id="upImgUpd" class="uploadTitle"></div>
					</div> 
				</td>
			</tr>
			
			<tr>
				<th>
					<label>备注：</label>
				</th>
				<td colspan="3">
					<textarea name="remark" class="form-control" v-model="partner.remark" maxlength="512"></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>

<inner-js>
	<script type="text/javascript" transient="true" src="/js/sys/partner.js"></script>
</inner-js>
