<table id="loadDialogLog" class="table-edit none">
	
	<tr>
		<th>
			<label>日志级别：</label>
		</th>
		<td>
			{{log.logLevel.desc}}
		</td>
		<th>
			<label>标题：</label>
		</th>
		<td>
			{{log.title}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>请求地址：</label>
		</th>
		<td>
			{{log.url}}
		</td>
		<th>
			<label>请求参数：</label>
		</th>
		<td>
			{{log.params}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>模块类型：</label>
		</th>
		<td>
			{{log.moduleType.desc}}
		</td>
		<th>
			<label>客户端IP：</label>
		</th>
		<td>
			{{log.clientIp}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>创建人：</label>
		</th>
		<td>
			{{log.creatorName}}
		</td>
		<th>
			<label>创建时间：</label>
		</th>
		<td>
			{{log.createTime | date}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>返回数据：</label>
		</th>
		<td colspan="3">
			<textarea class="form-control" readonly="readonly" autocomplete="off">{{log.result}}</textarea>
		</td>
	</tr>
	
	<tr>
		<th>
			<label>错误信息：</label>
		</th>
		<td colspan="3">
			<textarea class="form-control" readonly="readonly" autocomplete="off">{{log.errorMsg}}</textarea>
		</td>
	</tr>
</table>
