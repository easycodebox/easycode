<%@ page session="false" contentType="application/xml; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- 
	此重写页面增加了cas:attributes内容，当Cas的验证ticket协议升级为3.0后此页面则立马废弃，3.0已经包含了此功能。
	具体可查看\WEB-INF\view\jsp\protocol\3.0\casServiceValidationSuccess.jsp 
--%>

<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
    <cas:authenticationSuccess>
        <cas:user>${fn:escapeXml(principal.id)}</cas:user>
        <c:if test="${not empty pgtIou}">
            <cas:proxyGrantingTicket>${pgtIou}</cas:proxyGrantingTicket>
        </c:if>
        <c:if test="${fn:length(chainedAuthentications) > 0}">
            <cas:proxies>
                <c:forEach var="proxy" items="${chainedAuthentications}" varStatus="loopStatus" begin="0" end="${fn:length(chainedAuthentications)}" step="1">
                    <cas:proxy>${fn:escapeXml(proxy.principal.id)}</cas:proxy>
                </c:forEach>
            </cas:proxies>
        </c:if>
        <c:if test="${fn:length(principal.attributes) > 0}">
            <cas:attributes>
                <c:forEach var="attr"
                           items="${principal.attributes}"
                           varStatus="loopStatus" begin="0"
                           end="${fn:length(principal.attributes)}"
                           step="1">

                    <c:forEach var="attrval" items="${attr.value}">
                        <cas:${fn:escapeXml(attr.key)}>${fn:escapeXml(attrval)}</cas:${fn:escapeXml(attr.key)}>
                    </c:forEach>
                </c:forEach>
            </cas:attributes>
        </c:if>
    </cas:authenticationSuccess>
</cas:serviceResponse>
