<!DOCTYPE html>
<html>
<head>
[#include "/common/meta.ftl"/]
<title>500</title>
</head>
<body>
	<div>
		<h2 id="error-title">操作失败，请联系管理员...</h2>
		<p>异常信息：${message} -- (type=${error}, status=${status})</p>
		<p>异常类名：${exception}</p>
		<p>请求地址：${path}</p>
		<p>执行时间：${timestamp?datetime}</p>
		<pre id="error-trace">${trace}</pre>
	</div>
</body>
</html>
