<table id="loadDialogUser" class="table-edit none">
	
	<tr>
		<th>
			<label>用户组名：</label>
		</th>
		<td>
			{{user.groupName}}
		</td>
		<th>
			<label>员工编号：</label>
		</th>
		<td>
			{{user.userNo}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>用户名：</label>
		</th>
		<td>
			{{user.username}}
		</td>
		<th>
			<label>昵称：</label>
		</th>
		<td>
			{{user.nickname}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>真实姓名：</label>
		</th>
		<td>
			{{user.realname}}
		</td>
		<th>
			<label>状态：</label>
		</th>
		<td>
			{{user.status.desc}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>排序值：</label>
		</th>
		<td>
			{{user.sort}}
		</td>
		<th>
			<label>手机号：</label>
		</th>
		<td>
			{{user.mobile}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>性别：</label>
		</th>
		<td>
			{{user.gender.desc}}
		</td>
		<th>
			<label>邮箱：</label>
		</th>
		<td>
			{{user.email}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>错误登录：</label>
		</th>
		<td>
			{{user.loginFail}}
		</td>
		<th>
			<label>创建人：</label>
		</th>
		<td>
			{{user.creatorName}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>创建时间：</label>
		</th>
		<td>
			{{user.createTime | date}}
		</td>
		<th>
			<label>修改人：</label>
		</th>
		<td>
			{{user.modifierName}}
		</td>
	</tr>
	
	<tr>
		<th>
			<label>修改时间：</label>
		</th>
		<td>
			{{user.modifyTime | date}}
		</td>
	</tr>
</table>
