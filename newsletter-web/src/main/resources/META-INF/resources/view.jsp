<%--
    view.jsp: Default view of the newsletter-portlet.
    
    Created:     2016-10-05 15:54 by Christian Berndt
    Modified:    2017-03-10 16:43 by Christian Berndt
    Version:     1.1.4
 --%>

<%@ include file="/init.jsp"%>

<%@page import="ch.inofix.newsletter.exception.NewsletterReferencedByMailingException"%>
<%@page import="com.liferay.portal.kernel.security.auth.PrincipalException"%>

<%
    String backURL = ParamUtil.getString(request, "backURL");
    String tabs1 = ParamUtil.getString(request, "tabs1", "mailings");

//     PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameter("tabs1", tabs1);
    portletURL.setParameter("mvcPath", "/view.jsp");
    portletURL.setParameter("backURL", backURL);

    Log log = LogFactoryUtil.getLog("docroot.view.jsp");
%>

<div id="<portlet:namespace />newsletterContainer">

    <liferay-ui:header backURL="<%=backURL%>" title="newsletter-manager" />
    
    <liferay-ui:error exception="<%= PrincipalException.class %>" 
       message="you-dont-have-the-required-permissions"/>
       
    <liferay-ui:error exception="<%= NewsletterReferencedByMailingException.class %>"
        message="the-newsletter-is-referenced-by-one-or-more-mailings"/>    

    <liferay-ui:tabs names="mailings,newsletters,subscribers"
        param="tabs1" url="<%=portletURL.toString()%>" />

    <c:choose>
    
        <c:when test='<%=tabs1.equals("mailings")%>'>  
            <h1>Mailings</h1>      
<%--             <%@include file="/html/mailings.jspf"%> --%>
        </c:when>

        <c:when test='<%=tabs1.equals("newsletters")%>'>
            <h1>Newsletters</h1>
<%--             <%@include file="/html/newsletters.jspf"%> --%>
        </c:when>

        <c:otherwise> 
            <h1>Subscribers</h1>       
<%--             <%@include file="/html/subscribers.jspf"%> --%>
        </c:otherwise>

    </c:choose>

    <hr>

<!--     <ifx-util:build-info /> -->

</div>
