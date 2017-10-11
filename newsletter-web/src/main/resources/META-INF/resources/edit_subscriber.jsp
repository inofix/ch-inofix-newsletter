<%--
    edit_subscriber.jsp: edit a single subscriber.

    Created:     2017-09-22 00:07 by Christian Berndt
    Modified:    2017-10-11 23:21 by Christian Berndt
    Version:     1.0.2
--%>

<%@ include file="/init.jsp"%>

<%
    Subscriber subscriber = (Subscriber) request.getAttribute(NewsletterWebKeys.SUBSCRIBER);

    boolean disabled = false;

    String title = LanguageUtil.get(request, "new-subscriber");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (subscriber != null) {
        
        title = LanguageUtil.format(request, "edit-subscriber-x", String.valueOf(subscriber.getEmail()));

        hasUpdatePermission = SubscriberPermission.contains(permissionChecker, subscriber,
                NewsletterActionKeys.UPDATE);
        hasViewPermission = SubscriberPermission.contains(permissionChecker, subscriber, 
                NewsletterActionKeys.VIEW);
        hasDeletePermission = SubscriberPermission.contains(permissionChecker, subscriber,
                NewsletterActionKeys.DELETE);
        hasPermissionsPermission = SubscriberPermission.contains(permissionChecker, subscriber,
                NewsletterActionKeys.PERMISSIONS);

    } else {

        subscriber = SubscriberServiceUtil.createSubscriber();
        hasUpdatePermission = true;

    }
    
    boolean reverse = false;

    Sort sort = new Sort("title_sortable", reverse);

    Hits hits = NewsletterServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, 0, null, 0, 20, sort);
    
    List<Newsletter> newsletters = NewsletterUtil.getNewsletters(hits);
    
    if (newsletters.size() == 0) {
        disabled = true; 
    }
    
    String redirect = ParamUtil.getString(request, "redirect");

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(title);

    request.setAttribute("showTitle", "true"); // used by inofix-theme
%>

<div class="container-fluid-1280">

    <portlet:actionURL var="updateSubscriberURL">
        <portlet:param name="mvcPath" value="/edit_subscriber.jsp" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateSubscriberURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= Constants.UPDATE %>"/>
        <aui:input name="className" type="hidden"
            value="<%= Subscriber.class.getName() %>" />
        <aui:input name="userId" type="hidden"
            value="<%=String.valueOf(themeDisplay.getUserId())%>" />
    
        <aui:model-context bean="<%=subscriber%>"
            model="<%=Subscriber.class%>" />
    
        <div class="lfr-form-content">
        
            <aui:fieldset-group markupView="<%= markupView %>">
            
                <aui:input name="backURL" type="hidden"
                    value="<%=backURL%>" />

                <aui:input name="redirect" type="hidden"
                    value="<%=redirect%>" />

                <aui:input name="subscriberId" type="hidden"
                    disabled="<%=!hasUpdatePermission%>" />
                
                <aui:select name="newsletterId"
                    disabled="<%=disabled%>"
                    helpMessage="newsletter-id-help" label="newsletter"
                    inlineField="true">
                    <aui:option label="select-newsletter" value="0" />
                    <%
                        for (Newsletter newsletter : newsletters) {
                    %>
                    <aui:option label="<%=newsletter.getTitle()%>"
                        value="<%=newsletter.getNewsletterId()%>"
                        selected="<%=subscriber.getNewsletterId() == newsletter
                                            .getNewsletterId()%>" />
                    <%
                        }
                    %>
                </aui:select>
            
                <aui:input name="email"/>
                <aui:input name="salutation"/>
                <aui:input name="title"/>
                <aui:input name="firstname"/>
                <aui:input name="middlename"/>
                <aui:input name="lastname"/>                             
                <aui:input name="gender"/>
                        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
    
</div>
