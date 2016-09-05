<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1 ,minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="renderer" content="webkit">
<title>IC</title>
<link rel="shortcut icon" href="/themes/ic-dev/imgs/logo.ico" type="image/x-icon" />
<style>
body, div, span, h1, h2, h3, h4, h5, h6, p, em, img, strong, b, small, u, i, center, dl, dt, dd, ol, ul, li,  sub, sup, tt, var, del, dfn,  ins, kbd, q, s, samp,  strike, applet, object, iframe, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, article, aside, canvas, details, embed, figure, figcaption, footer, header, hgroup, menu, nav, output, ruby, section, summary, time, blockquote, pre, a, abbr, acronym, address, big, cite, code, mark, audio, video, input, textarea, select { margin:0; padding:0;}

ol, ul{list-style:none;}
body{ background:url(/themes/ic-backend/imgs/backendloginBg.jpg) no-repeat center top #519cbc; background-attachment:fixed; background-size:auto 100%; line-height:1;}

.backendbox .loginbox{ width:560px; height:102px; margin:20px auto 0; text-align:left;}
.backendbox .butbox{ height:32px; display:inline-block; overflow:hidden; float:left; margin-top:35px; margin-left:9px;}
.backendbox .ml{ margin-left:36px;}
.backendbox .butbox span{ display: block; width:34px; height:30px; text-align:center; line-height:34px; border:1px solid #092e53; background:#4483cf; border-radius:4px 0 0 4px; float:left;}
.backendbox .butbox span img{border-radius:4px 0 0 4px;}
.backendbox .butbox input{ width:146px; height:30px; line-height:30px; background:url(/themes/ic-backend/imgs/backend_input.jpg) repeat-x left top;border:1px solid #092e53; border-left:none; border-radius:0 4px 4px 0; padding:0 12px; font-size:14px;}
.backendbox .submit{ float:left; margin-top:35px; margin-left:10px;}
.backendbox .submit input{ width:52px; height:30px; border:1px solid #092e53; background:url(/themes/ic-backend/imgs/backend_icon_sub8.jpg) no-repeat center center; border-radius:4px;}
.backendbox .text{ font-size:12px; color:#fff; text-align:center; text-shadow:2px 2px 1px #666; margin-top:12px;}
</style>
</head>
	
<body class="loginBG">


<div class="backendbox" style=" text-align:center; margin-top:300px;">
	<div class="logo"><a href="#"><img src="/themes/ic-backend/imgs/backendlogo.png"></a></div>
	
	<form:form method="post" id="fm1" class="login_form"
		commandName="${commandName}" htmlEscape="true">
		<div class="errors" style="display: none;">
			<form:errors path="*" id="msg" element="div" delimiter="" />
		</div>
		
	    <div class="loginbox" style="background:url(/themes/ic-backend/imgs/backendloginbox.jpg) no-repeat center top;">
	    	<div class="butbox ml">
	        	<span><img src="/themes/ic-backend/imgs/backend_icon_user.jpg"></span>
	        	
	        	<c:choose>
	                <c:when test="${not empty sessionScope.openIdLocalId}">
	                    <strong><c:out value="${sessionScope.openIdLocalId}" /></strong>
	                    <input type="hidden" id="username" name="username" value="<c:out value="${sessionScope.openIdLocalId}" />" />
	                </c:when>
	                <c:otherwise>
	                    <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
	                    <form:input cssClass="required shuru" cssErrorClass="error" id="username" size="25" placeholder="账号" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" />
	                </c:otherwise>
	            </c:choose>
	            
	        </div>
	        <div class="butbox">
	        	<span><img src="/themes/ic-backend/imgs/backend_icon_pwd.jpg"></span>
	            
	            <spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
				<form:password cssClass="required shuru" cssErrorClass="error"
					id="password" size="25" placeholder="密码" tabindex="2"
					path="password" accesskey="${passwordAccessKey}"
					htmlEscape="true" autocomplete="off" />
	            
	        </div>
	        <div class="submit">
	            <input type="hidden" name="lt" value="${loginTicket}" /> 
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" /> 
				<input class="forbutom button btn_log" name="submit" accesskey="l" value="" tabindex="4" type="submit" />
	        </div>
	    </div>
	</form:form>
    <div class="text">Copyright &copy;2016 easycodebox.com</div>
</div>

<script type="text/javascript" src="http://cdn.easycodebox.com/js/jquery/jquery-1.7.2.js"></script>
<script type="text/javascript" src="/themes/ic-backend/js/cas.js">
{msg : "${param.msg}"}
</script>
</body>
</html>
