<table id="loadDialogIdGenerator" class="table-edit none">
	<tr>
		<th>
			<label>策略：</label>
		</th>
		<td>
			{{idGenerator.id}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>初始值：</label>
		</th>
		<td>
			{{idGenerator.initialVal}}
		</td>
		<th>
			<label>当前值：</label>
		</th>
		<td>
			{{idGenerator.currentVal}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>最大值：</label>
		</th>
		<td>
			{{idGenerator.maxVal}}
		</td>
		<th>
			<label>批次容量：</label>
		</th>
		<td>
			{{idGenerator.fetchSize}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>增长值：</label>
		</th>
		<td>
			{{idGenerator.increment}}
		</td>
		<th>
			<label>是否循环：</label>
		</th>
		<td>
			{{idGenerator.isCycle.desc}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>创建人：</label>
		</th>
		<td>
			{{idGenerator.creatorName}}
		</td>
		<th>
			<label>创建时间：</label>
		</th>
		<td>
			{{idGenerator.createTime | date}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>修改人：</label>
		</th>
		<td>
			{{idGenerator.modifierName}}
		</td>
		<th>
			<label>修改时间：</label>
		</th>
		<td>
			{{idGenerator.modifyTime | date}}
		</td>
	</tr>
</table>
