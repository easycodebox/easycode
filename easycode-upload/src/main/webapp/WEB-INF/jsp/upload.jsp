<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<meta http-equiv="Pragma" CONTENT="no-cache" />
<meta http-equiv="Cache-Control" CONTENT="no-cache" />
<meta http-equiv="Expires" CONTENT="0" />
<title>上传文件</title>
<script type="text/javascript" src="http://libs.useso.com/js/jquery/2.0.3/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	$("input[name=changeType]:eq(0)").prop("checked", true);
	$("input[name=changeType]").change(function() {
		$("#fileKey").attr("name", $(this).val());
	});
});
</script>
</head>
<body>

	<div style="margin: 0 auto;width: 800px;padding-top: 200px;">
		
		<% String full = request.getRequestURL().toString();%>
		<% String uri = request.getRequestURI().toString();%>
		<% String contextPath = request.getContextPath();%>
		<% String servletPath = request.getServletPath();%>
		<% String basePath = request.getScheme() + "://"
						+ request.getServerName() + ":" + request.getServerPort()
						+ request.getContextPath();%>
						
		RequestURL == <%=full%> 
		<br />
		RequestURI == <%=uri%> 
		<br />
		contextPath == <%=contextPath%> 
		<br />
		servletPath == <%=servletPath%> 
		<br />
		BasePath == <%=basePath%>
		<br /><br />
		<form action="<%=path%>/upload" method="post" enctype="multipart/form-data" >
			类型：<label><input type="radio" name="changeType" value="PIC_TYPE" checked="checked" /> 图片</label> 
				<label><input type="radio" name="changeType" value="MIX_TYPE" /> 混合</label> 
			<br><br>
			文件KEY： <input type="text" id="fileKey" name="PIC_TYPE" />
			<br><br>
			文件1： <input type="file" name="files" /> 
			<br><br>
			<!-- 文件2： <input type="file" name="files" /> -->
			<br>
			<input type="submit" value="提交" />
		</form>
	
	</div>

</body>
</html>