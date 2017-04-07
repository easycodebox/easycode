<!DOCTYPE HTML>
<html>
<head>
[#include "/common/meta.ftl"/]
<title>文件上传</title>
</head>
<body>
	<div style="margin: 0 auto;width: 800px;padding-top: 200px;">
		<form action="/upload" method="post" enctype="multipart/form-data" >
			类型：<label><input type="radio" name="changeType" value="PIC_TYPE" checked="checked" /> 图片</label>
			<label><input type="radio" name="changeType" value="MIX_TYPE" /> 混合</label>
			<input type="hidden" id="fileType" name="fileType" value="PIC_TYPE">
			<br><br>
			文件KEY： <input type="text" id="fileKey" name="fileKey" />
			<br><br>
			文件1： <input type="file" name="files" />
			<br><br>
			<!-- 文件2： <input type="file" name="files" /> -->
			<br>
			<input type="submit" value="提交" />
		</form>
	</div>
	
	<script type="text/javascript" src="http://cdn.easycodebox.com/jquery/2.2.4/jquery.min.js"></script>
	<script type="text/javascript">
		$(function() {
			$("input[name=changeType]:eq(0)").prop("checked", true);
			$("input[name=changeType]").change(function() {
				$("#fileType").val($(this).val());
			});
		});
	</script>

</body>
</html>