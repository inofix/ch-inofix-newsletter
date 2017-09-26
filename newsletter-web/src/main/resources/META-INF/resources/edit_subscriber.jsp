<%--
    edit_subscriber.jsp: edit a single subscriber.

    Created:     2017-09-22 00:07 by Christian Berndt
    Modified:    2017-09-26 23:02 by Christian Berndt
    Version:     1.0.1
--%>

<%@ include file="/init.jsp"%>

<%
    Subscriber subscriber = (Subscriber) request.getAttribute(NewsletterWebKeys.SUBSCRIBER);


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
            
                <aui:input name="email"/>
                <aui:input name="salutation"/>
                <aui:input name="firstname"/>
                <aui:input name="middlename"/>
                <aui:input name="lastname"/>                             
                <aui:input name="gender"/>
                <aui:input name="newsletterId"/>
                <aui:input name="title"/>
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
    
</div>
