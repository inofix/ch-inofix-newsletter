<%--
    navigation.jsp: Default navigation of Inofix' newsletter manager.
    
    Created:     2017-08-30 23:12 by Christian Berndt
    Modified:    2017-09-11 23:01 by Christian Berndt
    Version:     1.0.1
--%>

<%@ include file="/init.jsp" %>

<%
    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameter("tabs1", "mailings");
    
    PortletURL newslettersURL = renderResponse.createRenderURL();
    newslettersURL.setParameter("tabs1", "newsletters"); 
    
    PortletURL subscribersURL = renderResponse.createRenderURL();
    subscribersURL.setParameter("tabs1", "subscribers"); 
    
    long formDateTime = ParamUtil.getLong(request, "formDate"); 
%>

<aui:nav-bar cssClass="collapse-basic-search" markupView="<%= markupView %>">

    <aui:nav cssClass="navbar-nav">
        <aui:nav-item href="<%= portletURL.toString() %>" label="mailings" selected="<%= "mailings".equals(tabs1) %>" />
        <aui:nav-item href="<%= newslettersURL.toString()  %>" label="newsletters" selected="<%= "newsletters".equals(tabs1) %>" />
        <aui:nav-item href="<%= subscribersURL.toString()  %>" label="subscribers" selected="<%= "subscribers".equals(tabs1) %>" />
    </aui:nav>

    <liferay-portlet:renderURL varImpl="searchURL"/>

    <aui:form action="<%= searchURL.toString() %>" cssClass="newsletter-search" name="searchFm">
         
        <c:if test="<%= formDateTime > 0 %>">           
            <div class="clear-message">
                <liferay-frontend:management-bar-button href='<%= portletURL.toString() %>' icon='times' label='clear' />      
                <aui:a cssClass="muted" href="<%= portletURL.toString() %>" label="clear-current-query-and-sorts"/>
            </div>
        </c:if> 
        
        <liferay-util:include page="/search_bar.jsp" servletContext="<%= application %>"/>      

    </aui:form>
    
</aui:nav-bar>
