<%--
    view_subscribers.jsp: the subscribers panel.
    
    Created:    2017-09-02 11:55 by Christian Berndt
    Modified:   2017-09-02 11:55 by Christian Berndt
    Version:    1.0.0
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

    SubscriberSearch searchContainer = new SubscriberSearch(renderRequest, "cur", portletURL);

    boolean reverse = false;
    if (searchContainer.getOrderByType().equals("desc")) {
        reverse = true;
    }

    Sort sort = new Sort(searchContainer.getOrderByCol(), reverse);

    SubscriberSearchTerms searchTerms = (SubscriberSearchTerms) searchContainer.getSearchTerms();

    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {

        hits = SubscriberServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getTitle(), null, status, null, searchTerms.isAndOperator(),
                searchContainer.getStart(), searchContainer.getEnd(), sort);

    } else {

        hits = SubscriberServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, ownerUserId,
                searchTerms.getKeywords(), searchContainer.getStart(), searchContainer.getEnd(), sort);

    }

    List<Subscriber> subscribers = SubscriberUtil.getSubscribers(hits);

    searchContainer.setResults(subscribers);
    searchContainer.setTotal(hits.getLength());

    EntriesChecker entriesChecker = new EntriesChecker(liferayPortletRequest, liferayPortletResponse);

    searchContainer.setRowChecker(entriesChecker);
%>

<div id="<portlet:namespace />subscriberContainer">

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
        <aui:input name="deleteSubscriberIds" type="hidden" />
                
        <liferay-ui:search-container
            id="subscribers"
            searchContainer="<%=searchContainer%>"
            var="subscriberSearchContainer">
            
            <liferay-ui:search-container-row
                className="ch.inofix.newsletter.model.Subscriber"
                modelVar="subscriber" keyProperty="subscriberId">
        
                <portlet:renderURL var="editURL">
                    <portlet:param name="redirect" value="<%=currentURL%>" />
                    <portlet:param name="subscriberId"
                        value="<%=String.valueOf(subscriber.getSubscriberId())%>" />
                    <portlet:param name="mvcPath" value="/edit_subscriber.jsp" />
                </portlet:renderURL>
        
                <portlet:renderURL var="viewURL">
                    <portlet:param name="redirect" value="<%=currentURL%>" />
                    <portlet:param name="subscriberId"
                        value="<%=String.valueOf(subscriber.getSubscriberId())%>" />
                    <portlet:param name="mvcPath" value="/edit_subscriber.jsp" />
                </portlet:renderURL>
        
                <%
                    request.setAttribute("editURL", editURL.toString());
                    request.setAttribute("viewURL", viewURL.toString());
        
                    boolean hasUpdatePermission = SubscriberPermission.contains(permissionChecker,
                            subscriber.getSubscriberId(), NewsletterActionKeys.UPDATE);
                    boolean hasViewPermission = SubscriberPermission.contains(permissionChecker,
                            subscriber.getSubscriberId(), NewsletterActionKeys.VIEW);
        
                    String detailURL = null;
        
                    if (hasUpdatePermission) {
                        detailURL = editURL.toString();
                    } else if (hasViewPermission) {
                        detailURL = viewURL.toString();
                    }
                %>
        
                <%@ include file="/subscriber_search_columns.jspf"%>
        
                <liferay-ui:search-container-column-jsp align="right"
                    cssClass="entry-action" path="/subscriber_action.jsp"
                    valign="top" />
        
            </liferay-ui:search-container-row>
        
            <liferay-ui:search-iterator displayStyle="<%=displayStyle%>"
                markupView="<%=markupView%>" />
        
        </liferay-ui:search-container>
    
    </aui:form>
</div>

<liferay-util:include page="/add_subscriber_button.jsp" servletContext="<%= application %>" />    
