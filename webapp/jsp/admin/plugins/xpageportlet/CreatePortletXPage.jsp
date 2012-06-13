<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="xPagePortlet" scope="session" class="fr.paris.lutece.plugins.xpageportlet.web.portlet.XPagePortletJspBean" />
<%
	xPagePortlet.init( request, xPagePortlet.RIGHT_MANAGE_ADMIN_SITE);
%>

<%= xPagePortlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
