<table id="loadDialogPartner" class="table-edit none">
	<tr>
		<th>
			<label>合作商名：</label>
		</th>
		<td>
			{{partner.name}}
		</td>
		<th>
			<label>密钥：</label>
		</th>
		<td>
			{{partner.partnerKey}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>网址：</label>
		</th>
		<td>
			{{partner.website}}
		</td>
		<th>
			<label>状态：</label>
		</th>
		<td>
			{{partner.status.desc}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>排序值：</label>
		</th>
		<td>
			{{partner.sort}}
		</td>
		<th>
			<label>合同：</label>
		</th>
		<td>
			<img :src="partner.contract | img 'r40c40'" />
		</td>
	</tr>
	
	<tr>
		<th>
			<label>创建人：</label>
		</th>
		<td>
			{{partner.creatorName}}
		</td>
		<th>
			<label>创建时间：</label>
		</th>
		<td>
			{{partner.createTime | date}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>修改人：</label>
		</th>
		<td>
			{{partner.modifierName}}
		</td>
		<th>
			<label>修改时间：</label>
		</th>
		<td>
			{{partner.modifyTime | date}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>备注：</label>
		</th>
		<td colspan="3">
			<textarea class="form-control" readonly="readonly" autocomplete="off">{{partner.remark}}</textarea>
		</td>
	</tr>
</table>
