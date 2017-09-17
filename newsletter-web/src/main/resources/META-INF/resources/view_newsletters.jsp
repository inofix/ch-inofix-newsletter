<%--
    view_newsletters.jsp: the newsletters panel.
    
    Created:    2017-09-02 11:55 by Christian Berndt
    Modified:   2017-09-17 13:25 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%
    String displayStyle = ParamUtil.getString(request, "displayStyle");

    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
    portletURL.setParameter("redirect", "");
    portletURL.setParameter("tabs1", tabs1);
    
    long ownerUserId = ParamUtil.getLong(request, "ownerUserId", -1);
    
    int status = ParamUtil.getInteger(request, "status");

    NewsletterSearch searchContainer = new NewsletterSearch(renderRequest, "cur", portletURL);

    boolean reverse = false;
    if (searchContainer.getOrderByType().equals("desc")) {
        reverse = true;
    }

    Sort sort = new Sort(searchContainer.getOrderByCol(), reverse);

    NewsletterSearchTerms searchTerms = (NewsletterSearchTerms) searchContainer.getSearchTerms();

    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {

        hits = NewsletterServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getTitle(), searchTerms.getFromAddress(), searchTerms.getFromName(), status,
                null, searchTerms.isAndOperator(), searchContainer.getStart(), searchContainer.getEnd(), sort);

    } else {

        hits = NewsletterServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getKeywords(), searchContainer.getStart(), searchContainer.getEnd(), sort);

    }

    List<Newsletter> newsletters = NewsletterUtil.getNewsletters(hits);

    searchContainer.setResults(newsletters);
    searchContainer.setTotal(hits.getLength());

    EntriesChecker entriesChecker = new EntriesChecker(liferayPortletRequest, liferayPortletResponse);

    searchContainer.setRowChecker(entriesChecker);
%>

<div id="<portlet:namespace />newsletterContainer">

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
        
        <aui:input name="<%= Constants.CMD %>" type="hidden"/>  
        <aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
        <aui:input name="deleteNewsletterIds" type="hidden" />
                
        <liferay-ui:search-container
            id="newsletters"
            searchContainer="<%=searchContainer%>"
            var="newsletterSearchContainer">
            
            <liferay-ui:search-container-row
                className="ch.inofix.newsletter.model.Newsletter"
                modelVar="newsletter" keyProperty="newsletterId">
        
                <portlet:renderURL var="editURL">
                    <portlet:param name="redirect" value="<%=currentURL%>" />
                    <portlet:param name="newsletterId"
                        value="<%=String.valueOf(newsletter.getNewsletterId())%>" />
                    <portlet:param name="mvcPath" value="/edit_newsletter.jsp" />
                </portlet:renderURL>
        
                <portlet:renderURL var="viewURL">
                    <portlet:param name="redirect" value="<%=currentURL%>" />
                    <portlet:param name="newsletterId"
                        value="<%=String.valueOf(newsletter.getNewsletterId())%>" />
                    <portlet:param name="mvcPath" value="/edit_newsletter.jsp" />
                </portlet:renderURL>
        
                <%
                    request.setAttribute("editURL", editURL.toString());
                    request.setAttribute("viewURL", viewURL.toString());
        
                    boolean hasUpdatePermission = NewsletterPermission.contains(permissionChecker,
                            newsletter.getNewsletterId(), NewsletterActionKeys.UPDATE);
                    boolean hasViewPermission = NewsletterPermission.contains(permissionChecker,
                            newsletter.getNewsletterId(), NewsletterActionKeys.VIEW);
        
                    String detailURL = null;
        
                    if (hasUpdatePermission) {
                        detailURL = editURL.toString();
                    } else if (hasViewPermission) {
                        detailURL = viewURL.toString();
                    }
                %>
        
                <%@ include file="/newsletter_search_columns.jspf"%>
        
                <liferay-ui:search-container-column-jsp align="right"
                    cssClass="entry-action" path="/newsletter_action.jsp"
                    valign="top" />
        
            </liferay-ui:search-container-row>
        
            <liferay-ui:search-iterator displayStyle="<%=displayStyle%>"
                markupView="<%=markupView%>" />
        
        </liferay-ui:search-container>
    
    </aui:form>
</div>

<liferay-util:include page="/add_newsletter_button.jsp" servletContext="<%= application %>" />    
