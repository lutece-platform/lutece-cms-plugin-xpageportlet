<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="xPagePortlet" scope="session" class="fr.paris.lutece.plugins.xpageportlet.web.portlet.XPagePortletJspBean" />

<%
	xPagePortlet.init( request, xPagePortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( xPagePortlet.doModify( request ) );
%>
