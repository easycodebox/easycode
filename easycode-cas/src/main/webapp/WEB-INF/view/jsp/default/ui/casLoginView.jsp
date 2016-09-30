<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:directive.include file="includes/top.jsp" />

<div class="login-box-body">

	<form:form method="post" commandName="${commandName}" htmlEscape="true">
		
		<form:errors path="*" id="msg" cssClass="login-box-msg errors" element="p" delimiter="" />
		
		<div class="form-group has-feedback">
			<c:choose>
                <c:when test="${not empty sessionScope.openIdLocalId}">
                    <strong><c:out value="${sessionScope.openIdLocalId}" /></strong>
                    <input type="hidden" id="username" name="username" value="<c:out value="${sessionScope.openIdLocalId}" />" />
                </c:when>
                <c:otherwise>
                    <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
                    <form:input cssClass="form-control" cssErrorClass="form-control input-error" id="username" placeholder="账号" 
                    	size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" 
                    	autocomplete="off" htmlEscape="true" />
                </c:otherwise>
            </c:choose>
			<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
		</div>
		<div class="form-group has-feedback">
			<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
			<form:password cssClass="form-control" cssErrorClass="form-control input-error"
				id="password" size="25" placeholder="密码" tabindex="2"
				path="password" accesskey="${passwordAccessKey}"
				htmlEscape="true" autocomplete="off" />
			<span class="glyphicon glyphicon-lock form-control-feedback"></span>
		</div>
		<div class="row">
			<div class="col-xs-8">
				<div class="checkbox icheck">
					<label> <input id="rememberMe" name="rememberMe" value="true" type="checkbox"> 记住我 </label>
				</div>
			</div>
			<!-- /.col -->
			<div class="col-xs-4">
				<input type="hidden" name="lt" value="${loginTicket}" /> 
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" /> 
				<input class="btn btn-primary btn-block btn-flat" name="submit" value="登  录" type="submit" />
			</div>
			<!-- /.col -->
		</div>
	</form:form>

	<a href="#">找回密码</a> | <a href="#" class="text-center">立即注册</a>

</div>

<jsp:directive.include file="includes/bottom.jsp" />
