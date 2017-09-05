<%--
    search.jsp: The extended search of the newsletter portlet.

    Created:     2017-09-02 07:41 by Christian Berndt
    Modified:    2017-09-02 07:41 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<%@page import="java.util.Collections"%>

<%@page import="javax.portlet.WindowState"%>

<%@page import="ch.inofix.newsletter.web.internal.search.MailingDisplayTerms"%>

<%@page import="com.liferay.portal.kernel.search.facet.collector.FacetCollector"%>
<%@page import="com.liferay.portal.kernel.search.facet.collector.TermCollector"%>
<%@page import="com.liferay.portal.kernel.search.facet.Facet"%>
<%@page import="com.liferay.portal.kernel.search.facet.MultiValueFacet"%>
<%@page import="com.liferay.portal.kernel.search.IndexerRegistryUtil"%>
<%@page import="com.liferay.portal.kernel.search.Indexer"%>
<%@page import="com.liferay.portal.kernel.search.SearchContextFactory"%>
<%@page import="com.liferay.portal.kernel.search.SearchContext"%>
<%@page import="com.liferay.portal.kernel.service.UserServiceUtil"%>
<%@page import="com.liferay.portal.kernel.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.comparator.UserLastNameComparator"%>
<%@page import="com.liferay.portal.kernel.workflow.WorkflowConstants"%>

<%
    MailingDisplayTerms displayTerms = new MailingDisplayTerms(renderRequest);
    int status = ParamUtil.getInteger(request, "status");
    
    int fromDateDay = ParamUtil.getInteger(request, "fromDateDay"); 
    int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth"); 
    int fromDateYear = ParamUtil.getInteger(request, "fromDateYear"); 
    Date fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);
    boolean ignoreFromDate = fromDate == null; 
    
    int untilDateDay = ParamUtil.getInteger(request, "untilDateDay"); 
    int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth"); 
    int untilDateYear = ParamUtil.getInteger(request, "untilDateYear"); 
    Date untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
    boolean ignoreUntilDate = untilDate == null; 
    
    List<User> users = new ArrayList<User>(); 
    
    SearchContext searchContext = SearchContextFactory.getInstance(request);
    Facet userIdFacet = new MultiValueFacet(searchContext); 
    userIdFacet.setFieldName("ownerUserId"); 
    searchContext.addFacet(userIdFacet);
    
    // remove facet attributes from context, since we need the field's index here
    searchContext.setAttribute("ownerUserId", null); 
    
    Indexer<Mailing> indexer = IndexerRegistryUtil.getIndexer(Mailing.class);
    Hits hits = indexer.search(searchContext);
    
    FacetCollector userIdCollector = userIdFacet.getFacetCollector(); 
    List<TermCollector> userIdTermCollectors = userIdCollector.getTermCollectors(); 
    
    for (TermCollector termCollector : userIdTermCollectors) {
        
        long selectUserId = GetterUtil.getLong(termCollector.getTerm()); 
        User selectUser = UserLocalServiceUtil.getUser(selectUserId); 
        
        users.add(selectUser); 
        
    }
    
    Collections.sort(users, new UserLastNameComparator(true));  

%>

<liferay-ui:search-toggle
    autoFocus="<%= false %>"
    buttonLabel="search" displayTerms="<%=displayTerms%>"
    id="toggle_id_mailing_search" markupView="<%=markupView%>">
<%-- 
<liferay-ui:search-toggle
    autoFocus="<%=windowState.equals(WindowState.MAXIMIZED)%>"
    buttonLabel="search" displayTerms="<%=displayTerms%>"
    id="toggle_id_mailing_search" markupView="<%=markupView%>">
--%>
    
    <aui:fieldset>
            
        <aui:input inlineField="<%=true%>"
            name="<%=MailingDisplayTerms.TITLE%>" size="20"
            value="<%=displayTerms.getTitle()%>" />
        
        <aui:select name="<%=MailingDisplayTerms.OWNER_USER_ID%>" inlineField="<%= true %>">
            <aui:option value="" label="any-user"/>
            <% for (User selectUser : users) { %>
                <aui:option value="<%= selectUser.getUserId() %>" label="<%= selectUser.getFullName() %>"/>
            <% } %>
        </aui:select>
            
        <aui:select name="status" inlineField="<%= true %>"
            last="true">
            <aui:option
                value="<%=WorkflowConstants.STATUS_ANY%>"
                selected="<%=WorkflowConstants.STATUS_ANY == status%>">
                <liferay-ui:message key="any" />
            </aui:option>
            <aui:option
                value="<%=WorkflowConstants.STATUS_APPROVED%>"
                selected="<%=WorkflowConstants.STATUS_APPROVED == status%>">
                <liferay-ui:message key="approved" />
            </aui:option>
            <aui:option
                value="<%=WorkflowConstants.STATUS_DENIED%>"
                selected="<%=WorkflowConstants.STATUS_DENIED == status%>">
                <liferay-ui:message key="denied" />
            </aui:option>
            <aui:option
                value="<%=WorkflowConstants.STATUS_DRAFT%>"
                selected="<%=WorkflowConstants.STATUS_DRAFT == status%>">
                <liferay-ui:message key="draft" />
            </aui:option>
            <aui:option
                value="<%=WorkflowConstants.STATUS_INACTIVE%>"
                selected="<%= WorkflowConstants.STATUS_INACTIVE == status %>">
                <liferay-ui:message key="inactive" />
            </aui:option>
            <aui:option
                value="<%= WorkflowConstants.STATUS_INCOMPLETE %>"
                selected="<%= WorkflowConstants.STATUS_INCOMPLETE == status %>">
                <liferay-ui:message key="incomplete" />
            </aui:option>
            <aui:option
                value="<%= WorkflowConstants.STATUS_PENDING %>"
                selected="<%= WorkflowConstants.STATUS_PENDING == status %>">
                <liferay-ui:message key="pending" />
            </aui:option>
        </aui:select>
    </aui:fieldset>
    
</liferay-ui:search-toggle>
