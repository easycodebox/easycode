[#include "/common/core.ftl"/]
<!DOCTYPE HTML>
<html>
<head>
[#include "/common/meta.ftl"/]
<title><sitemesh:write property='title' /></title>
[#include "/common/styles.ftl"/]
<sitemesh:write property='head' />
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

	[#include "/common/header.ftl"/]
	
	[#include "/common/left.ftl"/]
	
	<div id="pjax-container" class="content-wrapper">
		
		<sitemesh:write property='body' />
		
	</div>
	
	[#include "/common/footer.ftl"/]
	
	[#include "/common/control-sidebar.ftl"/]
	
</div>
[#include "/common/scripts.ftl"/]
<sitemesh:write property='inner-js' />
</body>
</html>