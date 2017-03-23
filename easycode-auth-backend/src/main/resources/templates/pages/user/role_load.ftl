<table id="loadDialogRole" class="table-edit none">
	
	<tr>
		<th>
			<label>角色名：</label>
		</th>
		<td>
			{{role.name}}
		</td>
		<th>
			<label>排序值：</label>
		</th>
		<td>
			{{role.sort}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>状态：</label>
		</th>
		<td>
			{{role.status.desc}}
		</td>
		<th>
			<label>创建人：</label>
		</th>
		<td>
			{{role.creatorName}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>创建时间：</label>
		</th>
		<td>
			{{role.createTime | date}}
		</td>
		<th>
			<label>修改人：</label>
		</th>
		<td>
			{{role.modifierName}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>修改时间：</label>
		</th>
		<td>
			{{role.modifyTime | date}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>描述：</label>
		</th>
		<td colspan="3">
			<textarea class="form-control" readonly="readonly" autocomplete="off">{{role.description}}</textarea>
		</td>
	</tr>
	
	<tr>
		<th>
			<label>备注：</label>
		</th>
		<td colspan="3">
			<textarea class="form-control" readonly="readonly" autocomplete="off">{{role.remark}}</textarea>
		</td>
	</tr>
</table>
