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
<link rel="stylesheet" type="text/css" href="/themes/ic-dev/css/public.css">
<link rel="stylesheet" type="text/css" href="/themes/ic-dev/css/style.css">
</head>
	
	<!-- 设置变量   -->
	<c:set var="projectUrl" value="http://ic.easycodebox.com" />  
	
<body class="loginBG">
<div id="page1">
	
	<div class="login_top"><img src="/themes/ic-dev/imgs/lo_logo.png"></div>
	<div class="login_box">
    	
    	<div class="tabbox">
    		<!-- 
        	<div class="tab_top">
            	<ul>
                	<li class="cur" data-item="1">账号密码登录</li>
                    <li data-item="2">快速登录</li>
                </ul>
            </div>
             -->
            <div class="tab_bottom">
            	<div class="tab_item" data-item="1">
                	
                	<form:form method="post" id="fm1" class="login_form"
						commandName="${commandName}" htmlEscape="true">
						<div class="errors">
							<form:errors path="*" id="msg" element="div" delimiter="" />
						</div>
	                    <div class="zclg">
	                    	<div class="input_group">
	                        	<span class="icon icon_user"></span>
	                        	<c:choose>
					                <c:when test="${not empty sessionScope.openIdLocalId}">
					                    <strong><c:out value="${sessionScope.openIdLocalId}" /></strong>
					                    <input type="hidden" id="username" name="username" value="<c:out value="${sessionScope.openIdLocalId}" />" />
					                </c:when>
					                <c:otherwise>
					                    <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
					                    <form:input cssClass="required shuru" cssErrorClass="error" id="username" placeholder="手机号/邮箱/用户名" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" />
					                </c:otherwise>
					            </c:choose>
	                        </div>
	                        <div class="input_group">
	                        	<span class="icon icon_pwd"></span>
	                        	<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
								<form:password cssClass="required shuru" cssErrorClass="error"
									id="password" size="25" placeholder="输入密码" tabindex="2"
									path="password" accesskey="${passwordAccessKey}"
									htmlEscape="true" autocomplete="off" />
	                        </div>
	                        <div class="check_group" style="margin-top:40px;">
	                        	<a href="${projectUrl }/findpwd" class="fr" style="color:rgba(61,147,236,0.8)">忘记密码?</a>
	                            <label>
	                            	<input type="checkbox" name="rememberMe" id="rememberMe" value="true" tabindex="5"  />
	                            	下次自动登录
	                            </label>
	                        </div>
	                        <div class="check_group" style="margin-top:16px;">
	                        	<input type="hidden" name="lt" value="${loginTicket}" /> 
								<input type="hidden" name="execution" value="${flowExecutionKey}" />
								<input type="hidden" name="_eventId" value="submit" /> 
								<input class="forbutom button btn_log" name="submit" accesskey="l" value="登  录" tabindex="4" type="submit" />
	                        </div>
	                        <p style="text-align:center; font-size:12px; color:#666; margin-top:2em;"><a href="${projectUrl }/login/add.html">还没有账户，马上注册</a></p>
	                    </div>
                    </form:form>
                </div>
                <div class="tab_item" data-item="2">
                	<!--  -->
                    <div class="zclg">
                    	<div class="input_group">
                        	<span class="icon icon_phone"></span>
                            <input type="text" placeholder="输入大陆地区手机号" class="shuru">
                        </div>
                        <div class="input_group">
                        	<span class="icon icon_veri"></span>
                            <input type="password" placeholder="输入验证码" class="shuru" style="width:200px;">
                            <span class="fr" id="gettime"><b></b><input type="button" value="点击发送验证码" class="fr"></span>
                            
                        </div>
                        
                        <div class="check_group" style="margin-top:46px;">
                        	<a href="#" class="button btn_log">登录</a>
                        </div>
                        <p style="text-align:center; font-size:12px; color:#666; margin-top:2em;"><a href="${projectUrl }/login/add.html">还没有账户，马上注册</a></p>
                    </div>
                    <!--  -->
                </div>
            </div>
        
        
        
        </div>
        <!--  -->
    </div>

</div>
<!--  -->
<div class="login_footer">
    <div class="footer">
    	<div class="page_container">
        <p>
            <a href="#">关于我们</a>|
            <a href="#">联系我们</a>|
            <a href="#">隐私声明</a>|
            <a href="#">法律声明</a>
        </p>
        <p>Copyright &copy;2016 easycodebox.com</p>
        </div>
    </div>
</div>
<script type="text/javascript" src="http://cdn.easycodebox.com/js/jquery/jquery-1.7.2.js"></script>
<script type="text/javascript" src="/themes/ic-dev/js/cas.js">
{msg : "${param.msg}"}
</script>
</body>
</html>
