[#include "/common/core.ftl"/]

<title>EasyCode | 主键生成器</title>

<section class="content-header">
	<h1>
		主键生成器 <small>主键生成器</small>
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
							<label>生成策略：</label>
							<input type="text" class="form-control" name="id" />
						</div>
						
						<div class="form-group">
							<label>是否循环：</label>
							[@e.radioEnum labelClass="radio-inline" name="isCycle" enumName="YesNo" /]
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
					</div>
					<table id="data-table"
							data-toolbar="#toolbar"
							data-query-form="query-form"
							data-query-params="gb.table.params"
							data-id-field="id"
							data-unique-id="id"
							data-select-item-name="ids"
							data-url="/idGenerator/list.json">
						<thead hidden>
							<tr>
								<th data-checkbox="true" data-click-to-select="true"></th>
								<th data-formatter="gb.table.fmtOrder" data-click-to-select="true">序号</th>
								<th data-field="id">生成策略</th>
								<th data-field="initialVal">初始值</th>
								<th data-field="currentVal">当前值</th>
								<th data-field="maxVal">最大值</th>
								<th data-field="fetchSize">批次容量</th>
								<th data-field="increment">增长值</th>
								<th data-field="isCycle" data-formatter="gb.table.fmtIsCycle">是否循环</th>
								<th data-field="creatorName">创建人</th>
								<th data-field="createTime" data-formatter="gb.table.fmtDate">创建时间</th>
								<th data-formatter="gb.table.fmtOps">操作</th>
							</tr>
						</thead>
					</table>
					<div id="table-ops" class="hidden">
						[@shiro.hasPermission name="idGenerator/load"]
							<a class="btn btn-xs btn-default loadBtn" data-id="{id}" title="详情" href="#" role="button">
								<i class="fa fa-reorder"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="idGenerator/update"]
							<a class="btn btn-xs btn-default updBtn" data-id="{id}" title="修改" href="#" role="button">
								<i class="fa fa-edit"></i>
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
	[#include "/pages/idGenerator_load.ftl"/]
	
	<!-- 修改 -->
	<form id="updDialog" class="form-validate none" action="/idGenerator/update.json" data-suc="更新主键生成器成功" data-fail="更新主键生成器失败">
	
		<input type="hidden" name="id" v-model="idGenerator.id" />
		
		<table class="table-edit">
			<tr>
				<th>
					<label class="required">初始值：</label>
				</th>
				<td>
					<input type="text" name="initialVal" class="form-control" v-model="idGenerator.initialVal" data-bvalidator="alphanum,gb.initialVal" maxlength="32" required />
				</td>
				<th>
					<label class="required">当前值：</label>
				</th>
				<td>
					<input type="hidden" class="oldCurVal" v-model="idGenerator.currentVal" />
					<input type="text" name="currentVal" class="form-control" v-model="idGenerator.currentVal" data-bvalidator="alphanum,gb.curVal" maxlength="32" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label>最大值：</label>
				</th>
				<td>
					<input type="text" name="maxVal" class="form-control" v-model="idGenerator.maxVal" data-bvalidator="alphanum,gb.maxVal" maxlength="32" />
				</td>
				<th>
					<label class="required">批次容量：</label>
				</th>
				<td>
					<input type="text" name="fetchSize" class="form-control" v-model="idGenerator.fetchSize" data-bvalidator="digit" maxlength="9" required />
				</td>
			</tr>
			
			<tr>
				<th>
					<label class="required">增长值：</label>
				</th>
				<td>
					<input type="text" name="increment" class="form-control" v-model="idGenerator.increment" data-bvalidator="digit" maxlength="9" required />
				</td>
				<th>
					<label class="required">是否循环：</label>
				</th>
				<td>
					[@e.radioEnum name="isCycle" enumName="YesNo"
							tagAttr='{
								last: "required data-bvalidator-msg=\'请选择是否循环\'",
								all: "v-model=\'idGenerator.isCycle.className\'"
							}'/]
				</td>
			</tr>
		</table>
	</form>
</div>
	
<inner-js>
	<script type="text/javascript" transient="true" src="/js/idGenerator.js"></script>
</inner-js>
