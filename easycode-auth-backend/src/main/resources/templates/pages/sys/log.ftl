[#include "/common/core.ftl"/]

<title>EasyCode | 日志</title>

<section class="content-header">
	<h1>
		日志 <small>日志</small>
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
							<label>标题：</label>
							<input type="text" class="form-control" name="title" />
						</div>
						
						<div class="form-group">
							<label>请求地址：</label>
							<input type="text" class="form-control" name="url" />
						</div>
						
						<div class="form-group">
							<label>请求参数：</label>
							<input type="text" class="form-control" name="params" />
						</div>
						
						<div class="form-group">
							<label>模块类型：</label>
							[@e.selectEnum cssClass="form-control" name="moduleType" enumName="ModuleType" 
												headerKey="" headerValue="--请选择--" /]
						</div>
						
						<div class="form-group">
							<label>日志级别：</label>
							[@e.selectEnum cssClass="form-control" name="logLevel" enumName="LogLevel" 
									headerKey="" headerValue="--请选择--" /]
						</div>
						
						<div class="form-group">
							<label>返回数据：</label>
							<input type="text" class="form-control" name="result" />
						</div>
						
						<div class="form-group">
							<label>客户端IP：</label>
							<input type="text" class="form-control" name="clientIp" />
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
						[@shiro.hasPermission name="log/removePhy"]
							<button type="button" title="批量删除" class="btn btn-default" onclick="gb.remove('/log/removePhy.json')">
                       			<i class="fa fa-remove"></i>
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
							data-url="/log/list.json">
						<thead hidden>
							<tr>
								<th data-checkbox="true" data-click-to-select="true"></th>
								<th data-formatter="gb.table.fmtOrder" data-click-to-select="true">序号</th>
								<th data-field="title">标题</th>
								<th data-field="method" data-visible="false">方法</th>
								<th data-field="url">请求地址</th>
								<th data-field="params" data-visible="false">请求参数</th>
								<th data-field="moduleType" data-formatter="gb.table.fmtEnum">模块类型</th>
								<th data-field="logLevel" data-formatter="gb.table.fmtEnum">日志级别</th>
								<th data-field="result" data-visible="false">返回数据</th>
								<th data-field="clientIp">客户端IP</th>
								<th data-field="creatorName">创建人</th>
								<th data-field="createTime" data-formatter="gb.table.fmtDate">创建时间</th>
								<th data-formatter="gb.table.fmtOps">操作</th>
							</tr>
						</thead>
					</table>
					<div id="table-ops" class="hidden">
						[@shiro.hasPermission name="log/load"]
							<a class="btn btn-xs btn-default loadBtn" data-id="{id}" title="详情" href="#" role="button">
								<i class="fa fa-reorder"></i>
							</a>
						[/@shiro.hasPermission]
						[@shiro.hasPermission name="log/removePhy"]
							<a class="btn btn-xs btn-default" href="javascript:gb.remove('/log/removePhy.json', '{id}');" title="删除" role="button">
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
	[#include "/pages/sys/log_load.ftl"/]
</div>

<inner-js>
	<script type="text/javascript" transient="true" src="/js/sys/log.js"></script>
</inner-js>
