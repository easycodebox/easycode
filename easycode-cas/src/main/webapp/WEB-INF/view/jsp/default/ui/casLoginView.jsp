<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:directive.include file="includes/top.jsp" />

<%-- <div class="mailogo">
	<img src="<spring:theme code='project.logo.title' />" style="height: 60px;" />
</div> --%>

<div class="maforms">
	<div class="formimgs">
		<!-- <img src="" style="display: none;" /> -->
	</div>
	<div class="forminfo">
		<div class="loginmain">
			<form:form method="post" id="fm1" class="login_form"
				commandName="${commandName}" htmlEscape="true">
				<div class="errors">
					<form:errors path="*" id="msg" element="div" delimiter="" />
				</div>
				<div class="login_txt">登录名：</div>
				<div class="input-box">
					<div class="icon-wrapper">
						<div class="icon_login un"></div>
					</div>
					<c:choose>
		                <c:when test="${not empty sessionScope.openIdLocalId}">
		                    <strong><c:out value="${sessionScope.openIdLocalId}" /></strong>
		                    <input type="hidden" id="username" name="username" value="<c:out value="${sessionScope.openIdLocalId}" />" />
		                </c:when>
		                <c:otherwise>
		                    <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
		                    <form:input cssClass="required" cssErrorClass="error" id="username" placeholder="用户名" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" />
		                </c:otherwise>
		            </c:choose>
				</div>
				<div class="login_txt">登录密码：</div>
				<div class="input-box">
					<div class="icon-wrapper">
						<div class="icon_login pwd"></div>
					</div>
					<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
					<form:password cssClass="required" cssErrorClass="error"
						id="password" size="25" placeholder="密码" tabindex="2"
						path="password" accesskey="${passwordAccessKey}"
						htmlEscape="true" autocomplete="off" />
				</div>
				<div class="login_operate">
					<div class="fobutom">
						<input type="hidden" name="lt" value="${loginTicket}" /> 
						<input type="hidden" name="execution" value="${flowExecutionKey}" />
						<input type="hidden" name="_eventId" value="submit" /> 
						<input class="forbutom" name="submit" accesskey="l" value="登  录" tabindex="4" type="submit" />
					</div>
					<div class="forget_pwd">
						<!--<input id="rememberMe" name="rememberMe" value="true" type="checkbox" />记住我	-->
						<!--<a href="#">忘记密码?</a>-->
					</div>
				</div>
				<div class="register">
					<!-- <p>还没有DSP账户？</p>
					<a>点击注册</a>-->
				</div>
			</form:form>
		</div>
	</div>
</div>
	
<jsp:directive.include file="includes/bottom.jsp" />
