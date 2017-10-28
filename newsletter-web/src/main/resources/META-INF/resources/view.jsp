<%--
    view.jsp: Default view of the newsletter-portlet.
    
    Created:     2016-10-05 15:54 by Christian Berndt
    Modified:    2017-10-28 19:50 by Christian Berndt
    Version:     1.2.0
 --%>

<%@ include file="/init.jsp"%>

<%
    String backURL = ParamUtil.getString(request, "backURL");

    PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("tabs1", tabs1);
    portletURL.setParameter("mvcPath", "/view.jsp");
    portletURL.setParameter("backURL", backURL);

    Log log = LogFactoryUtil.getLog("docroot.view.jsp");
%>

<liferay-util:include page="/navigation.jsp"
    servletContext="<%=application%>"/>

<liferay-ui:error exception="<%=PrincipalException.class%>"
    message="you-dont-have-the-required-permissions" />

<liferay-ui:error
    exception="<%= NewsletterReferencedByMailingException.class %>"
    message="the-newsletter-is-referenced-by-one-or-more-mailings" />
    
<c:choose>             

    <c:when test='<%=tabs1.equals("mailings")%>'>
        <liferay-util:include page="/view_mailings.jsp" servletContext="<%= application %>" />
    </c:when>

    <c:when test='<%=tabs1.equals("newsletters")%>'>
        <liferay-util:include page="/view_newsletters.jsp" servletContext="<%= application %>" />
    </c:when>

    <c:otherwise>
        <liferay-util:include page="/view_subscribers.jsp" servletContext="<%= application %>" />
    </c:otherwise>

</c:choose>        
