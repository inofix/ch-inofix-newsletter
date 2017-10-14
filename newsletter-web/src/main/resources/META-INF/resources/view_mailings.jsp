<%--
    view_mailings.jsp: the mailings panel.
    
    Created:    2017-09-02 09:03 by Christian Berndt
    Modified:   2017-10-14 14:27 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%@page import="ch.inofix.newsletter.web.internal.search.MailingEntriesChecker"%>

<%
    // TODO: read mailing columns from configuration
    String[] columns = {"title", "newsletter", "user-name", "modified-date", "send-date"}; 

    String displayStyle = ParamUtil.getString(request, "displayStyle");

    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
    portletURL.setParameter("redirect", "");
    portletURL.setParameter("tabs1", tabs1);
    
    long ownerUserId = ParamUtil.getLong(request, "ownerUserId", -1);
    
    int status = ParamUtil.getInteger(request, "status");

    MailingSearch searchContainer = new MailingSearch(renderRequest, "cur", portletURL);

    boolean reverse = false;
    if (searchContainer.getOrderByType().equals("desc")) {
        reverse = true;
    }

    Sort sort = new Sort(searchContainer.getOrderByCol(), reverse);

    MailingSearchTerms searchTerms = (MailingSearchTerms) searchContainer.getSearchTerms();

    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {

        hits = MailingServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getTitle(), status, null, searchTerms.isAndOperator(),
                searchContainer.getStart(), searchContainer.getEnd(), sort);

    } else {

        hits = MailingServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getKeywords(), searchContainer.getStart(), searchContainer.getEnd(), sort);

    }

    List<Mailing> mailings = MailingUtil.getMailings(hits);

    searchContainer.setResults(mailings);
    searchContainer.setTotal(hits.getLength());
    
    request.setAttribute("view_mailings.jsp-columns", columns);
    request.setAttribute("view_mailings.jsp-total", hits.getLength());

    MailingEntriesChecker entriesChecker = new MailingEntriesChecker(liferayPortletRequest, liferayPortletResponse);

    searchContainer.setRowChecker(entriesChecker);
%>

<liferay-util:include page="/mailing_toolbar.jsp" servletContext="<%= application %>">
    <liferay-util:param name="searchContainerId" value="mailings" />
</liferay-util:include>

<div id="<portlet:namespace />mailingContainer">

    <liferay-ui:error exception="<%= PrincipalException.class %>"
        message="you-dont-have-the-required-permissions" />
     
    <c:if test="<%= showSearchSpeed %>">  
        <div class="alert alert-info">
            <liferay-ui:search-speed hits="<%= hits %>" searchContainer="<%= searchContainer %>"/>
        </div>
    </c:if> 
        
    <portlet:actionURL var="editSetURL"/>
    
    <aui:form action="<%= editSetURL %>" name="fm" 
        onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "editSet();" %>'>
        
        <aui:input name="className" type="hidden"/>
        <aui:input name="<%= Constants.CMD %>" type="hidden"/>  
        <aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
        <aui:input name="deleteMailingIds" type="hidden" />
                
        <liferay-ui:search-container
            id="mailings"
            searchContainer="<%=searchContainer%>"
            var="mailingSearchContainer">
            
            <liferay-ui:search-container-row
                className="ch.inofix.newsletter.model.Mailing"
                modelVar="mailing" keyProperty="mailingId">
        
                <portlet:renderURL var="editURL">
                    <portlet:param name="redirect" value="<%=currentURL%>" />
                    <portlet:param name="mailingId"
                        value="<%=String.valueOf(mailing.getMailingId())%>" />
                    <portlet:param name="mvcPath" value="/edit_mailing.jsp" />
                </portlet:renderURL>
        
                <portlet:renderURL var="viewURL">
                    <portlet:param name="redirect" value="<%=currentURL%>" />
                    <portlet:param name="mailingId"
                        value="<%=String.valueOf(mailing.getMailingId())%>" />
                    <portlet:param name="mvcPath" value="/edit_mailing.jsp" />
                </portlet:renderURL>
        
                <%
                    request.setAttribute("editURL", editURL.toString());
                    request.setAttribute("viewURL", viewURL.toString());
        
                    boolean hasUpdatePermission = MailingPermission.contains(permissionChecker,
                            mailing.getMailingId(), NewsletterActionKeys.UPDATE);
                    boolean hasViewPermission = MailingPermission.contains(permissionChecker,
                            mailing.getMailingId(), NewsletterActionKeys.VIEW);
        
                    String detailURL = null;
        
                    if (hasUpdatePermission) {
                        detailURL = editURL.toString();
                    } else if (hasViewPermission) {
                        detailURL = viewURL.toString();
                    }
                %>
        
                <%@ include file="/mailing_search_columns.jspf"%>
        
                <liferay-ui:search-container-column-jsp align="right"
                    cssClass="entry-action" path="/mailing_action.jsp"
                    valign="top" />
        
            </liferay-ui:search-container-row>
        
            <liferay-ui:search-iterator displayStyle="<%=displayStyle%>"
                markupView="<%=markupView%>" />
        
        </liferay-ui:search-container>
    
    </aui:form>
</div>

<liferay-util:include page="/add_mailing_button.jsp" servletContext="<%= application %>" />
