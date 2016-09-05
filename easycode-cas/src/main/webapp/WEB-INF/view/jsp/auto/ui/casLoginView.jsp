<!DOCTYPE html>
<!-- 
	此页面为测试页面，当请求的URL为：http://sso.xxx.com/login?themeFrame=auto&service=http://auth.xxx.com/user/list&un=username&ps=password
	时，会自动登录。当网络慢时，此空白页面停留时间较长，不建议正式环境使用。
 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
<head>
<meta charset="UTF-8">
<title>SSO</title>
<script type="text/javascript">
function doAutoLogin() {
    document.forms[0].submit();
}
</script>
</head>
<body onload="doAutoLogin();" style="display: none;">

	<form id="fm1" method="post" action="<%= request.getContextPath() %>/login?service=<%= request.getParameter("service") %>">
		<input type="text" name="username" value="${param.un}" />
		<input type="password" name="password" value="${param.ps}" />
		<input type="hidden" name="lt" value="${loginTicket}" /> 
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="_eventId" value="submit" /> 
		<% if ("true".equals(request.getParameter("rememberMe"))) {%>
            <input type="hidden" name="rememberMe" value="true" />
        <% } %>
		<input type="submit" value="submit" />
	</form>
</body>
</html>
