<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="renderer" content="webkit">
<meta http-equiv="Pragma" CONTENT="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Expires" CONTENT="0">
<title>500</title>
</head>
<body>
	<div class="error_page">
		<h1 id="msg-info">操作失败，请联系管理员...</h1>
		<div id="msg-exception"></div>
	</div>
</body>
<script type="text/javascript">
	var name = "CODE_MSG",
		codeMsg = null;
	if (document.cookie && document.cookie != '') {
	    var cookies = document.cookie.split(';');
	    for (var i = 0; i < cookies.length; i++) {
	        var cookie = cookies[i].trim();
	        if (cookie.substring(0, name.length + 1) == (name + '=')) {
	        	codeMsg = JSON.parse(decodeURIComponent(cookie.substring(name.length + 1)));
	            break;
	        }
	    }
	}
	if(codeMsg) {
		if (codeMsg.msg) {
			document.getElementById("msg-info").innerHTML = codeMsg.msg;
		}
		if (codeMsg.data) {
			document.getElementById("msg-exception").innerHTML = codeMsg.data;
		}
        //删除cookie
        var options = {
        		path: "/"
        	},
        	now = new Date();
        now.setTime(now.getTime() + (-1 * 24 * 60 * 60 * 1000));
        
       	var expires = '; expires=' + now.toUTCString(), //use expires attribute, max-age is not supported by IE
        	path = options.path ? '; path=' + options.path : '',
        	domain = options.domain ? '; domain=' + options.domain : '',
        	secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', '', expires, path, domain, secure].join('');
	}
</script>
</html>
