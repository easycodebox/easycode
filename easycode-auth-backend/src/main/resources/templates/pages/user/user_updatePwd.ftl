<form id="updPwdDialog" class="form-validate none" autocomplete="off"
	action="/user/updatingPwd.json" data-suc="更新密码成功" data-fail="更新密码失败">
	<table class="table-edit">
		
		<tr>
			<th>
				<label class="required">原密码：</label>
			</th>
			<td>
				<input type="password" name="oldPwd" class="form-control" maxlength="32" required />
			</td>
		</tr>
		
		<tr>
			<th>
				<label class="required">新密码：</label>
			</th>
			<td>
				<input type="password" id="pwd" name="pwd" class="form-control" maxlength="32" required />
			</td>
		</tr>
		
		<tr>
			<th>
				<label class="required">确认密码：</label>
			</th>
			<td>
				<input type="password" class="form-control" data-bvalidator="equal[pwd]" maxlength="32" required />
			</td>
		</tr>
	</table>
</form>
